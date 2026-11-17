package ru.cwcode.commands.executor;

import revxrsal.asm.MethodCaller;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.messages.MessageReturn;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public abstract class AbstractAutowiredExecutor extends AbstractExecutor {
  private final MethodTypeNode rootNode = new MethodTypeNode();
  private final List<String> definitionProblems = new ArrayList<>();

  public AbstractAutowiredExecutor() {
    Method[] methods = this.getClass().getDeclaredMethods();
    // getDeclaredMethods() не задаёт порядок — сортировка делает выбор между подходящими методами стабильным
    Arrays.sort(methods, Comparator.comparing(Method::toGenericString));

    for (Method declaredMethod : methods) {
      if (declaredMethod.isSynthetic()) continue;

      Method replaced = rootNode.register(declaredMethod);
      if (replaced != null) {
        definitionProblems.add(l10n.get("executor.validation.duplicateSignature",
                                        getClass().getName(), replaced.getName(), declaredMethod.getName(),
                                        typeNames(declaredMethod.getParameterTypes()), declaredMethod.getName()));
      }
    }
  }

  @Override
  public void executeForPlayer() {
    Argument[] args = parser.args;
    int argsLength = args.length;

    Object[] mapped = new Object[argsLength];
    Class<?>[] mappedTypes = new Class<?>[argsLength];
    int mappedParametersCount = 0;

    for (Argument arg : args) {
      if (arg instanceof ExactStringArg) continue;

      Object map = arg.map();
      mapped[mappedParametersCount] = map;
      mappedTypes[mappedParametersCount] = map == null ? null : map.getClass();
      mappedParametersCount++;
    }

    MethodCaller foundedMethod = rootNode.find(mappedTypes, mappedParametersCount);
    if (foundedMethod == null) {
      throw new MessageReturn(l10n.get("error.noSuchMethod", CollectionUtils.toString(Arrays.stream(mappedTypes)
                                                                                            .filter(Objects::nonNull)
                                                                                            .map(Class::getSimpleName)
                                                                                            .collect(Collectors.toList()))));
    }

    try {
      foundedMethod.call(this, Arrays.copyOf(mapped, mappedParametersCount));
    } catch (Exception e) {
      Throwable cause = e.getCause();

      if (cause instanceof Exception) {
        handleError((Exception) cause);
        return;
      }

      handleError(e);
    }
  }

  @Override
  public boolean acceptsArgumentCount(int count) {
    return rootNode.hasMethodAtDepth(count);
  }

  @Override
  public List<String> definitionProblems() {
    return definitionProblems;
  }

  private static String typeNames(Class<?>[] types) {
    return Arrays.stream(types).map(Class::getSimpleName).collect(Collectors.joining(", "));
  }

  private static class MethodTypeNode {
    private MethodCaller caller;
    private Method method;
    private final Map<Class<?>, MethodTypeNode> children = new LinkedHashMap<>();

    /**
     * @return метод с теми же типами параметров, который был вытеснен, или null
     */
    Method register(Method method) {
      return register(method.getParameterTypes(), method, 0);
    }

    private Method register(Class<?>[] parameterTypes, Method method, int level) {
      if (level == parameterTypes.length) {
        Method replaced = this.method;
        this.method = method;
        this.caller = MethodCaller.wrap(method);
        return replaced;
      }

      return children.computeIfAbsent(parameterTypes[level], __ -> new MethodTypeNode())
                     .register(parameterTypes, method, level + 1);
    }

    MethodCaller find(Class<?>[] parameterTypes, int depth) {
      return find(parameterTypes, 0, depth);
    }

    private MethodCaller find(Class<?>[] parameterTypes, int level, int depth) {
      if (level == depth) {
        return caller;
      }

      Class<?> type = parameterTypes[level];
      MethodTypeNode exact = type == null ? null : children.get(type);

      if (exact != null) {
        MethodCaller result = exact.find(parameterTypes, level + 1, depth);
        if (result != null) return result;
      }

      for (Map.Entry<Class<?>, MethodTypeNode> entry : children.entrySet()) {
        if (entry.getValue() == exact || !accepts(entry.getKey(), type)) continue;

        MethodCaller result = entry.getValue().find(parameterTypes, level + 1, depth);
        if (result != null) return result;
      }

      return null;
    }

    /**
     * null — значение аргумента, чей {@code map()} вернул null: подходит в любой ссылочный параметр.
     */
    private static boolean accepts(Class<?> parameterType, Class<?> valueType) {
      if (valueType == null) return !parameterType.isPrimitive();
      return parameterType.isAssignableFrom(valueType);
    }

    boolean hasMethodAtDepth(int depth) {
      if (depth == 0) return caller != null;

      for (MethodTypeNode child : children.values()) {
        if (child.hasMethodAtDepth(depth - 1)) return true;
      }
      return false;
    }
  }
}
