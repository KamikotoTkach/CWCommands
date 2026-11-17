package ru.cwcode.commands.executor;

import net.kyori.adventure.audience.Audience;
import revxrsal.asm.MethodCaller;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.messages.MessageReturn;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

/**
 * Исполнитель, который сам выбирает метод по типам значений аргументов набора.
 * <ul>
 *   <li>Кандидаты — {@code public} нестатические методы самого класса.</li>
 *   <li>Первый параметр типа отправителя ({@code Sender}, {@code Audience}, платформенный игрок или
 *   отправитель) получает отправителя, если метода с полной сигнатурой нет. Метод с игроком первым
 *   параметром отвечает не-игроку «только для игроков».</li>
 *   <li>Пропущенный optional-аргумент без значения по умолчанию приходит как {@code null}
 *   или пустой {@code Optional<T>}, если метода на укороченный набор нет.</li>
 * </ul>
 * Приоритет: полная сигнатура раньше подстановки отправителя, внутри каждой — точное число значений
 * раньше дополнения null-ами.
 */
public abstract class AbstractAutowiredExecutor extends AbstractExecutor {
  private final MethodTypeNode explicitRoot = new MethodTypeNode();
  private final MethodTypeNode senderRoot = new MethodTypeNode();
  private final List<String> definitionProblems = new ArrayList<>();

  public AbstractAutowiredExecutor() {
    Method[] methods = this.getClass().getDeclaredMethods();
    // getDeclaredMethods() не задаёт порядок — сортировка делает выбор между подходящими методами стабильным
    Arrays.sort(methods, Comparator.comparing(Method::toGenericString));

    for (Method method : methods) {
      if (!isCandidate(method)) continue;

      MethodCaller caller = MethodCaller.wrap(method);
      report(explicitRoot.register(DispatchMethod.explicit(method, caller)), method);

      SenderParameter senderParameter = method.getParameterCount() == 0
                                        ? SenderParameter.NONE
                                        : senderParameter(method.getParameterTypes()[0]);
      if (senderParameter != SenderParameter.NONE) {
        report(senderRoot.register(DispatchMethod.withSender(method, caller, senderParameter)), method);
      }
    }
  }

  private static boolean isCandidate(Method method) {
    int modifiers = method.getModifiers();
    return !method.isSynthetic() && !method.isBridge() && Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
  }

  private void report(DispatchMethod replaced, Method method) {
    if (replaced == null) return;

    definitionProblems.add(l10n.get("executor.validation.duplicateSignature",
                                    getClass().getName(), replaced.method.getName(), method.getName(),
                                    replaced.parameterNames(), method.getName()));
  }

  /**
   * Может ли первый параметр метода получать отправителя. Вызывается из конструктора — без состояния.
   */
  protected SenderParameter senderParameter(Class<?> type) {
    return type == Sender.class || type == Audience.class ? SenderParameter.ANY : SenderParameter.NONE;
  }

  /**
   * Отправитель текущего вызова в виде типа {@code type}, для которого {@link #senderParameter} не NONE.
   */
  protected Object senderValue(Class<?> type) {
    return type == Sender.class ? sender : sender.getAudience();
  }

  @Override
  public void executeForPlayer() {
    Argument[] args = parser.args;

    Object[] mapped = new Object[args.length];
    Class<?>[] mappedTypes = new Class<?>[args.length + parser.omitted];
    int count = 0;

    for (Argument arg : args) {
      if (arg instanceof ExactStringArg) continue;

      Object map = arg.map();
      mapped[count] = map;
      mappedTypes[count] = map == null ? null : map.getClass();
      count++;
    }

    DispatchMethod target = find(mappedTypes, count, parser.omitted);
    if (target == null) {
      throw new MessageReturn(l10n.get("error.noSuchMethod", CollectionUtils.toString(Arrays.stream(mappedTypes)
                                                                                            .filter(Objects::nonNull)
                                                                                            .map(Class::getSimpleName)
                                                                                            .collect(Collectors.toList()))));
    }

    Object senderArgument = null;
    if (target.senderType != null) {
      if (target.senderParameter == SenderParameter.PLAYER && !sender.isPlayer()) {
        throw new MessageReturn(l10n.get("precondition.onlyForPlayer.cannotExecute"));
      }
      senderArgument = senderValue(target.senderType);
    }

    try {
      target.caller.call(this, target.arguments(senderArgument, mapped, count));
    } catch (Exception e) {
      Throwable cause = e.getCause();

      if (cause instanceof Exception) {
        handleError((Exception) cause);
        return;
      }

      handleError(e);
    }
  }

  /**
   * Типы после {@code count} — null: так пропущенные аргументы подходят в любой ссылочный параметр.
   * Подстановка отправителя — только если полной сигнатуры нет ни на одной длине: иначе
   * {@code f(Player target, Optional<Player> other)} на «/cmd bob» получил бы отправителя вместо bob.
   */
  private DispatchMethod find(Class<?>[] types, int count, int omitted) {
    DispatchMethod found = find(explicitRoot, types, count, omitted);
    return found != null ? found : find(senderRoot, types, count, omitted);
  }

  private static DispatchMethod find(MethodTypeNode root, Class<?>[] types, int count, int omitted) {
    for (int depth = count; depth <= count + omitted; depth++) {
      DispatchMethod found = root.find(types, depth);
      if (found != null) return found;
    }
    return null;
  }

  @Override
  public boolean acceptsArgumentCount(int count) {
    return acceptsArgumentCount(count, 0);
  }

  @Override
  public boolean acceptsArgumentCount(int count, int omitted) {
    for (int depth = count; depth <= count + omitted; depth++) {
      if (explicitRoot.hasMethodAtDepth(depth) || senderRoot.hasMethodAtDepth(depth)) return true;
    }
    return false;
  }

  @Override
  public List<String> definitionProblems() {
    return definitionProblems;
  }

  public enum SenderParameter {
    /**
     * Обычный параметр-значение.
     */
    NONE,
    /**
     * Получает отправителя любого вида.
     */
    ANY,
    /**
     * Получает игрока; не-игроку команда отвечает «только для игроков».
     */
    PLAYER
  }

  private static class MethodTypeNode {
    private DispatchMethod method;
    private final Map<Class<?>, MethodTypeNode> children = new LinkedHashMap<>();

    /**
     * @return метод с теми же типами значений, который был вытеснен, или null
     */
    DispatchMethod register(DispatchMethod method) {
      return register(method.valueTypes, method, 0);
    }

    private DispatchMethod register(Class<?>[] valueTypes, DispatchMethod method, int level) {
      if (level == valueTypes.length) {
        DispatchMethod replaced = this.method;
        this.method = method;
        return replaced;
      }

      return children.computeIfAbsent(valueTypes[level], __ -> new MethodTypeNode())
                     .register(valueTypes, method, level + 1);
    }

    DispatchMethod find(Class<?>[] parameterTypes, int depth) {
      return find(parameterTypes, 0, depth);
    }

    private DispatchMethod find(Class<?>[] parameterTypes, int level, int depth) {
      if (level == depth) {
        return method;
      }

      Class<?> type = parameterTypes[level];
      MethodTypeNode exact = type == null ? null : children.get(type);

      if (exact != null) {
        DispatchMethod result = exact.find(parameterTypes, level + 1, depth);
        if (result != null) return result;
      }

      for (Map.Entry<Class<?>, MethodTypeNode> entry : children.entrySet()) {
        if (entry.getValue() == exact || !accepts(entry.getKey(), type)) continue;

        DispatchMethod result = entry.getValue().find(parameterTypes, level + 1, depth);
        if (result != null) return result;
      }

      return null;
    }

    /**
     * null — значение аргумента, чей {@code map()} вернул null, или пропущенный аргумент:
     * подходит в любой ссылочный параметр.
     */
    private static boolean accepts(Class<?> parameterType, Class<?> valueType) {
      if (valueType == null) return !parameterType.isPrimitive();
      return parameterType.isAssignableFrom(valueType);
    }

    boolean hasMethodAtDepth(int depth) {
      if (depth == 0) return method != null;

      for (MethodTypeNode child : children.values()) {
        if (child.hasMethodAtDepth(depth - 1)) return true;
      }
      return false;
    }
  }
}
