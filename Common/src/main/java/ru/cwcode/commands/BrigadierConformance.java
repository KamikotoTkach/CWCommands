package ru.cwcode.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static ru.cwcode.commands.preconditions.PreconditionRequirements.CAN_PERFORM_AND_CAN_SEE;

/**
 * Прогоняет команду через настоящий {@link CommandDispatcher} с деревом {@link BrigadierTree} и сверяет
 * с нашим разбором без исполнения: исполнитель только запоминает аргументы.
 * <ul>
 *   <li><b>ошибка</b> — Brigadier отверг ввод, передал парсеру не те слова или потерял нашу подсказку
 *   (после {@link BrigadierTree#keepOurs}, как на сервере): игрок увидит не то, что видел раньше;</li>
 *   <li><b>предупреждение</b> — сбой в коде аргумента при подборе примеров;</li>
 *   <li><b>подсветка</b> — допустимое значение не прошло клиентский тип и ушло в хвост: работает, но клиент
 *   не раскрасит аргументы; лечится {@link Argument#clientShape()}.</li>
 * </ul>
 */
public final class BrigadierConformance {
  private static final int SAMPLES_PER_ARGUMENT = 2;
  private static final List<String> FOREIGN_WORDS = List.of("x:y", "абв", "#ff00ff", "-1", "99999999999");

  private final Command root;
  private final String label;
  private final Sender sender;
  private final CommandDispatcher<Sender> dispatcher = new CommandDispatcher<>();
  private final TabCompleter tabCompleter;
  private final AtomicReference<String[]> executed = new AtomicReference<>();
  private final Report report;
  private final Set<String> probed = new LinkedHashSet<>();

  private BrigadierConformance(Command root, Sender sender) {
    this.root = root;
    this.label = root.getName();
    this.sender = sender;
    this.tabCompleter = new TabCompleter(root) {};
    this.report = new Report(label);

    BrigadierTree<Sender> tree = new BrigadierTree<>(source -> source,
                                                     (source, args) -> executed.set(args),
                                                     (source, args) -> tabCompleter.onTabComplete(source, args),
                                                     source -> false);
    dispatcher.getRoot().addChild(tree.build(root, label, source -> true));
  }

  public static Report check(Command root, Sender sender) {
    BrigadierConformance conformance = new BrigadierConformance(root, sender);
    conformance.walk(root, new ArrayList<>());
    return conformance.report;
  }

  private void walk(Command command, List<String> path) {
    suggest(path, "");
    execute(path, false);
    execute(with(path, "extra", "words"), false);

    for (Command subcommand : command.subcommands) {
      if (!visible(() -> subcommand.checkPreconditions(sender, CAN_PERFORM_AND_CAN_SEE), with(path, subcommand.name))) continue;
      walk(subcommand, with(path, subcommand.name));
      for (String alias : subcommand.aliases) {
        execute(with(path, alias), false);
        suggest(with(path, alias), "");
      }
      execute(with(path, subcommand.name.toUpperCase()), false);
    }

    for (ArgumentSet set : command.argumentSets) {
      if (visible(() -> set.checkPreconditions(sender, CAN_PERFORM_AND_CAN_SEE), with(path, set.toString()))) walkSet(set, path);
    }
  }

  /**
   * Условие, которое бросает для этого отправителя (каст консоли к игроку), сломает и наш таб-комплит —
   * это предупреждение, а ветку проверка пропускает.
   */
  private boolean visible(BooleanSupplier check, List<String> where) {
    try {
      return check.getAsBoolean();
    } catch (RuntimeException e) {
      report.warnings.add("/" + label + " " + String.join(" ", where) + " — условие видимости бросает " + e);
      return false;
    }
  }

  private void walkSet(ArgumentSet set, List<String> path) {
    List<String> written = new ArrayList<>();

    for (int i = 0; i < set.arguments.length; i++) {
      List<String> prefix = concat(path, written);
      suggest(prefix, "");

      for (String foreign : FOREIGN_WORDS) {
        execute(with(prefix, foreign), false);
      }

      List<String> samples = samples(set, i, written);
      if (samples.isEmpty()) return;

      for (String sample : samples) {
        if (!sample.isEmpty()) suggest(prefix, sample.substring(0, 1));
      }

      for (String sample : samples.subList(1, samples.size())) {
        List<String> alternative = new ArrayList<>(written);
        alternative.add(sample);
        if (alternative.size() == set.arguments.length) execute(concat(path, alternative), true);
      }
      written.add(samples.get(0));
    }

    execute(concat(path, written), true);
    execute(with(concat(path, written), "extra"), false);
  }

  /**
   * Допустимые значения аргумента из его же подсказок; для literal — само слово.
   */
  private List<String> samples(ArgumentSet set, int index, List<String> written) {
    Argument argument = set.arguments[index];
    if (argument instanceof ExactStringArg) return List.of(((ExactStringArg) argument).getExactString());

    boolean spaced = set.spacedLastArgument && index == set.arguments.length - 1;
    List<String> samples = new ArrayList<>();
    List<String> candidates = new ArrayList<>();

    try {
      Collection<String> completions = argument.completions(sender, with(written, ""));
      if (completions != null) candidates.addAll(completions);
    } catch (RuntimeException e) {
      report.warnings.add("подсказки аргумента «" + argument.argumentName() + "» бросают " + e);
    }
    candidates.addAll(List.of("1", "0", "true", "test"));

    for (String candidate : candidates) {
      if (samples.size() >= SAMPLES_PER_ARGUMENT) break;
      if (candidate == null || candidate.isEmpty() || samples.contains(candidate)) continue;
      if (!spaced && candidate.indexOf(' ') >= 0) continue;
      try {
        if (argument.valid(sender, candidate, with(written, candidate))) samples.add(candidate);
      } catch (RuntimeException e) {
        report.warnings.add("проверка аргумента «" + argument.argumentName() + "» на «" + candidate + "» бросает " + e);
        break;
      }
    }
    return samples;
  }

  private void execute(List<String> words, boolean valid) {
    String typed = String.join(" ", words);
    String input = typed.isEmpty() ? label : label + " " + typed;
    if (!probed.add("execute " + input)) return;
    report.inputs++;

    executed.set(null);
    try {
      dispatcher.execute(input, sender);
    } catch (CommandSyntaxException e) {
      report.errors.add("/" + input + " — Brigadier отказал: " + e.getMessage());
      return;
    } catch (RuntimeException e) {
      report.errors.add("/" + input + " — исполнение бросило " + e);
      return;
    }

    String[] expected = typed.isEmpty() ? new String[0] : typed.split(" ");
    String[] actual = executed.get();
    if (actual == null || !Arrays.equals(actual, expected)) {
      report.errors.add("/" + input + " — парсер получил " + (actual == null ? "ничего" : Arrays.toString(actual))
                        + " вместо " + Arrays.toString(expected));
      return;
    }

    if (valid) {
      String tailFrom = tailStart(dispatcher.parse(input, sender));
      if (tailFrom != null) report.highlighting.add("/" + input + " — с «" + tailFrom + "» уходит в хвост");
    }
  }

  private static String tailStart(ParseResults<Sender> parse) {
    CommandContextBuilder<Sender> context = parse.getContext().getLastChild();
    for (ParsedCommandNode<Sender> node : context.getNodes()) {
      if (BrigadierTree.TAIL.equals(node.getNode().getName())) {
        return node.getRange().get(parse.getReader().getString());
      }
    }
    return null;
  }

  private void suggest(List<String> words, String partial) {
    String typed = String.join(" ", with(words, partial));
    String input = label + " " + typed;
    if (!probed.add("suggest " + input)) return;
    report.inputs++;

    Set<String> ours;
    Set<String> brigadier = new LinkedHashSet<>();
    try {
      ours = new LinkedHashSet<>(tabCompleter.onTabComplete(sender, typed.split(" ", -1)));
    } catch (RuntimeException e) {
      report.warnings.add("/" + input + " — наши подсказки бросают " + e);
      return;
    }

    int offset = input.lastIndexOf(' ') + 1;
    try {
      Suggestions server = dispatcher.getCompletionSuggestions(dispatcher.parse(input, sender)).join();
      for (Suggestion suggestion : BrigadierTree.keepOurs(input, server, ours).getList()) {
        String applied = suggestion.apply(input);
        if (applied.length() < offset || !applied.startsWith(input.substring(0, offset))) {
          report.errors.add("/" + input + " — подсказка «" + suggestion.getText() + "» заменяет не последнее слово");
          continue;
        }
        brigadier.add(applied.substring(offset));
      }
    } catch (RuntimeException e) {
      report.errors.add("/" + input + " — подсказки Brigadier бросили " + e);
      return;
    }

    Set<String> missing = new LinkedHashSet<>(ours);
    missing.removeAll(brigadier);
    missing.remove(partial); // Brigadier не подсказывает уже набранное слово целиком — так было и до дерева
    if (!missing.isEmpty()) report.errors.add("/" + input + " — пропали подсказки " + missing);
  }

  private static List<String> with(List<String> list, String... more) {
    List<String> copy = new ArrayList<>(list);
    copy.addAll(Arrays.asList(more));
    return copy;
  }

  private static List<String> concat(List<String> first, List<String> second) {
    List<String> copy = new ArrayList<>(first);
    copy.addAll(second);
    return copy;
  }

  public static final class Report {
    private final String label;
    int inputs;
    final List<String> errors = new ArrayList<>();
    final List<String> warnings = new ArrayList<>();
    final List<String> highlighting = new ArrayList<>();

    Report(String label) {
      this.label = label;
    }

    public String label() {
      return label;
    }

    public int inputs() {
      return inputs;
    }

    public List<String> errors() {
      return errors;
    }

    public List<String> warnings() {
      return warnings;
    }

    public List<String> highlighting() {
      return highlighting;
    }

    public boolean clean() {
      return errors.isEmpty();
    }

    @Override
    public String toString() {
      return "/" + label + ": проверок " + inputs + ", ошибок " + errors.size() + ", предупреждений " + warnings.size()
             + ", мимо подсветки " + highlighting.size();
    }
  }
}
