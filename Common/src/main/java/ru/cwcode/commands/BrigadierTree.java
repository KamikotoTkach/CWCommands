package ru.cwcode.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static ru.cwcode.commands.preconditions.PreconditionRequirements.CAN_PERFORM_AND_CAN_SEE;

/**
 * Дерево Brigadier для клиента: подкоманды и {@link ExactStringArg} — literal, аргументы — типы из
 * {@link Argument#clientShape()}. Дерево только показывает команду: исполнение и подсказки на любом
 * узле — наши {@link CommandParser} и {@link TabCompleter} по всей введённой строке.
 * <p>
 * У каждого узла последним ребёнком стоит «хвост» {@value #TAIL} — greedy-строка без условий.
 * Brigadier выбирает его, когда типизированный путь не разобрался (опечатка, значение не по клиентскому
 * типу, скрытый набор, ветка, спрятанная {@code requires}), поэтому любой ввод доходит до нашего парсера
 * и игрок видит наши сообщения, а не ванильное «Incorrect argument». Подсказки отдаёт тоже хвост.
 * <p>
 * {@code requires} узла — «видно и можно» хотя бы для одного набора или подкоманды под ним, как у
 * {@link TabCompleter}, но <b>только при построении дерева для клиента</b> ({@code buildsClientTree}).
 * При разборе на сервере он всегда истинен: Brigadier, встретив слово, равное literal, смотрит только
 * на этот literal, и скрытая ветка дала бы ванильную ошибку вместо нашего сообщения об отказе.
 * Решение «можно ли» всегда за нашим парсером.
 * <p>
 * Серверные подсказки Brigadier собирает со всех детей узла без {@code requires}, то есть вместе со
 * скрытыми literal. Платформа оставляет из них только те, что дал наш {@link TabCompleter} —
 * {@link #keepOurs}.
 *
 * @param <S> источник команды платформы
 */
public final class BrigadierTree<S> {
  public static final String TAIL = "...";

  private final Function<S, Sender> senders;
  private final BiConsumer<S, String[]> executor;
  private final BiFunction<S, String[], List<String>> completer;
  private final Predicate<S> buildsClientTree;

  /**
   * @param executor         исполнение: аргументы после метки команды, как их отдал бы Bukkit
   * @param completer        подсказки: аргументы после метки, последний — недописанное слово (может быть пустым)
   * @param buildsClientTree истина, когда {@code requires} спрашивают ради дерева для клиента, а не ради разбора
   */
  public BrigadierTree(Function<S, Sender> senders, BiConsumer<S, String[]> executor, BiFunction<S, String[], List<String>> completer,
                       Predicate<S> buildsClientTree) {
    this.senders = senders;
    this.executor = executor;
    this.completer = completer;
    this.buildsClientTree = buildsClientTree;
  }

  /**
   * Оставляет из серверных подсказок к строке {@code input} (без «/») только наши — последнее слово
   * после применения подсказки должно быть среди {@code ours}.
   */
  public static Suggestions keepOurs(String input, Suggestions suggestions, Collection<String> ours) {
    int offset = input.lastIndexOf(' ') + 1;
    Set<String> allowed = new HashSet<>(ours);
    List<Suggestion> kept = new ArrayList<>();
    for (Suggestion suggestion : suggestions.getList()) {
      String applied = suggestion.apply(input);
      if (applied.length() >= offset && allowed.contains(applied.substring(offset))) kept.add(suggestion);
    }
    return kept.size() == suggestions.getList().size() ? suggestions : new Suggestions(suggestions.getRange(), kept);
  }

  public LiteralCommandNode<S> build(Command command, String label, Predicate<S> requirement) {
    Plan root = new Plan(label, null);
    plan(root, command);

    LiteralCommandNode<S> node = LiteralArgumentBuilder.<S>literal(label)
                                                      .requires(requirement)
                                                      .executes(this::run)
                                                      .build();
    attach(node, root);
    return node;
  }

  /**
   * Аргументы после метки команды для исполнения. Метка — первый узел последнего контекста, так что
   * алиасы и {@code /execute … run} разбираются одинаково.
   */
  public static String[] executionArgs(CommandContext<?> context) {
    CommandContext<?> last = context.getLastChild();
    String input = last.getInput();
    int from = Math.min(labelEnd(last), input.length());
    return splitWords(input.substring(from));
  }

  /**
   * Аргументы после метки для подсказок: строка до курсора, пустые слова сохраняются, как в Bukkit.
   */
  public static String[] completionArgs(CommandContext<?> context, SuggestionsBuilder builder) {
    String input = builder.getInput();
    int from = Math.min(labelEnd(context.getLastChild()), input.length());
    String typed = input.substring(from);
    if (typed.startsWith(" ")) typed = typed.substring(1);
    return typed.split(" ", -1);
  }

  private static int labelEnd(CommandContext<?> context) {
    return context.getNodes().isEmpty() ? context.getRange().getStart() : context.getNodes().get(0).getRange().getEnd();
  }

  private static String[] splitWords(String text) {
    List<String> words = new ArrayList<>();
    for (String word : text.split(" ")) {
      if (!word.isEmpty()) words.add(word);
    }
    return words.toArray(new String[0]);
  }

  private int run(CommandContext<S> context) {
    executor.accept(context.getSource(), executionArgs(context));
    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
  }

  private CompletableFuture<Suggestions> suggest(CommandContext<S> context, SuggestionsBuilder builder) {
    List<String> found = completer.apply(context.getSource(), completionArgs(context, builder));
    SuggestionsBuilder lastWord = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);
    if (found != null) {
      for (String suggestion : found) {
        lastWord.suggest(suggestion);
      }
    }
    return lastWord.buildFuture();
  }

  private void plan(Plan node, Command command) {
    for (Command subcommand : command.subcommands) {
      Predicate<Sender> visible = sender -> subcommand.checkPreconditions(sender, CAN_PERFORM_AND_CAN_SEE);

      List<String> names = new ArrayList<>();
      names.add(subcommand.name);
      names.addAll(subcommand.aliases);

      for (String name : names) {
        if (!isLiteral(name)) continue;
        Plan child = node.child(name, null);
        child.visibleTo.add(visible);
        plan(child, subcommand);
      }
    }

    for (ArgumentSet set : command.argumentSets) {
      Predicate<Sender> visible = sender -> set.checkPreconditions(sender, CAN_PERFORM_AND_CAN_SEE);
      Plan current = node;

      for (int i = 0; i < set.arguments.length; i++) {
        Argument argument = set.arguments[i];

        if (argument instanceof ExactStringArg) {
          String literal = ((ExactStringArg) argument).getExactString();
          if (!isLiteral(literal)) break; // дальше разбирает хвост
          current = current.child(literal, null);
        } else {
          boolean spaced = set.spacedLastArgument && i == set.arguments.length - 1;
          current = current.child(nodeName(argument), spaced ? ClientShape.GREEDY : argument.clientShape());
        }

        current.visibleTo.add(visible);
        if (current.isGreedy()) break;
      }
    }
  }

  private static String nodeName(Argument argument) {
    String name = argument.argumentName();
    return name == null || name.trim().isEmpty() ? "arg" : name;
  }

  private static boolean isLiteral(String name) {
    return name != null && !name.isEmpty() && name.indexOf(' ') < 0;
  }

  private void attach(CommandNode<S> parent, Plan plan) {
    if (plan.isGreedy()) return;

    Set<String> taken = new HashSet<>();
    taken.add(TAIL);
    for (Plan child : plan.children.values()) {
      if (child.isLiteral()) taken.add(child.name);
    }

    for (Plan child : plan.children.values()) {
      CommandNode<S> node = child.isLiteral()
                            ? LiteralArgumentBuilder.<S>literal(child.name).requires(requirement(child)).executes(this::run).build()
                            : argument(unique(child.name, taken), type(child.shape)).requires(requirement(child)).executes(this::run).build();
      parent.addChild(node);
      attach(node, child);
    }

    parent.addChild(argument(TAIL, StringArgumentType.greedyString()).suggests(this::suggest).executes(this::run).build());
  }

  /**
   * Сбой условия (например, оно трогает мир не с главного потока) показывает ветку: упавший
   * {@code requires} оставил бы игрока без дерева команд целиком.
   */
  private Predicate<S> requirement(Plan plan) {
    List<Predicate<Sender>> visibleTo = plan.visibleTo;
    return source -> {
      if (visibleTo.isEmpty() || !buildsClientTree.test(source)) return true;
      try {
        Sender sender = senders.apply(source);
        for (Predicate<Sender> visible : visibleTo) {
          if (visible.test(sender)) return true;
        }
        return false;
      } catch (RuntimeException e) {
        return true;
      }
    };
  }

  /**
   * Имена детей узла уникальны: Brigadier сливает одноимённых соседей, и у аргумента разных типов
   * остался бы тип первого.
   */
  private static String unique(String name, Set<String> taken) {
    String candidate = name;
    for (int i = 2; !taken.add(candidate); i++) {
      candidate = name + "_" + i;
    }
    return candidate;
  }

  @SuppressWarnings("unchecked")
  private RequiredArgumentBuilder<S, Object> argument(String name, ArgumentType<?> type) {
    return RequiredArgumentBuilder.argument(name, (ArgumentType<Object>) type);
  }

  private static ArgumentType<?> type(ClientShape shape) {
    switch (shape.kind()) {
      case INTEGER:
        return IntegerArgumentType.integer((int) shape.min(), (int) shape.max());
      case DOUBLE:
        return DoubleArgumentType.doubleArg(shape.min(), shape.max());
      case BOOLEAN:
        return BoolArgumentType.bool();
      case GREEDY:
        return StringArgumentType.greedyString();
      default:
        return StringArgumentType.word();
    }
  }

  /**
   * Узел будущего дерева: одинаковые подкоманды и аргументы разных наборов сливаются, условия видимости
   * копятся. Literal — {@code shape == null}.
   */
  private static final class Plan {
    final String name;
    final ClientShape shape;
    final List<Predicate<Sender>> visibleTo = new ArrayList<>();
    final Map<String, Plan> children = new LinkedHashMap<>();

    Plan(String name, ClientShape shape) {
      this.name = name;
      this.shape = shape;
    }

    Plan child(String name, ClientShape shape) {
      String key = shape == null ? "literal:" + name : "argument:" + name + ":" + shape;
      return children.computeIfAbsent(key, ignored -> new Plan(name, shape));
    }

    boolean isLiteral() {
      return shape == null;
    }

    boolean isGreedy() {
      return shape != null && shape.kind() == ClientShape.Kind.GREEDY;
    }
  }
}
