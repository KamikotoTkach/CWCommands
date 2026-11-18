package ru.cwcode.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.cwcode.commands.CommandRuntimeTest.Deny;
import ru.cwcode.commands.CommandRuntimeTest.TestSender;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.arguments.basic.BooleanArg;
import ru.cwcode.commands.arguments.basic.DoubleArg;
import ru.cwcode.commands.arguments.basic.IntegerArg;
import ru.cwcode.commands.arguments.basic.StringArg;
import ru.cwcode.commands.arguments.spaced.SpacedStringArg;
import ru.cwcode.commands.test.L10PlatformTest;
import ru.cwcode.commands.test.TestConfigPlatform;
import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.cwutils.l10n.L10n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BrigadierTreeTest {
  enum Mode {ON, OFF}

  @BeforeAll
  static void before() {
    L10PlatformTest l10PlatformTest = new L10PlatformTest();
    CommandsAPI.setPlatform(new TestConfigPlatform());
    CommandsAPI.setL10n(new L10n(l10PlatformTest));
    CommandsAPI.setConfig(new SimpleConfig("config", l10PlatformTest));
  }

  /**
   * Команда со всем, что встречается в проекте: подкоманды с алиасами, literal первым аргументом,
   * числа с границами, enum, optional со значением, spaced, одноимённые аргументы разных типов,
   * literal и аргумент с одним именем, отказ по precondition, requireConfirm.
   */
  static Command rich() {
    CommandRuntimeTest.TestExecutor executor = new CommandRuntimeTest.Recorder.NoArgs();
    Command command = new Command("rich")
      .arguments(new ArgumentSet(executor, new ExactStringArg("give"), new StringArg("player"), new IntegerArg("count").setMin(1).setMax(64)),
                 new ArgumentSet(executor, new ExactStringArg("give"), new StringArg("player"), new StringArg("count")),
                 new ArgumentSet(executor, new ExactStringArg("mode"), new EnumArg(Mode.values(), "mode"), new BooleanArg().optional("true")),
                 new ArgumentSet(executor, new ExactStringArg("say"), new SpacedStringArg("text")),
                 new ArgumentSet(executor, new ExactStringArg("speed"), new DoubleArg("value").setMin(0).setMax(10)),
                 new ArgumentSet(executor, new StringArg("player"), new StringArg("give")),
                 new ArgumentSet(executor, new ExactStringArg("secret")).preconditions(new Deny()),
                 new ArgumentSet(executor, new ExactStringArg("wipe"), new StringArg("world")).requireConfirm())
      .subCommands(new Command("list", executor).aliases("ls", "Список"),
                   new Command("admin").subCommands(new Command("reload", executor))
                                        .arguments(new ArgumentSet(executor, new IntegerArg("page"))),
                   new Command("hidden", executor).preconditions(new Deny()));
    command.register();
    return command;
  }

  @Test
  void richCommandBehavesLikeOurParser() {
    BrigadierConformance.Report report = BrigadierConformance.check(rich(), new TestSender("alice"));

    assertTrue(report.inputs() > 50, report.toString());
    assertEquals(List.of(), report.errors(), report.toString());
  }

  @Test
  void consoleSeesTheSameAsOurParser() {
    BrigadierConformance.Report report = BrigadierConformance.check(rich(), new TestSender("console", false));

    assertEquals(List.of(), report.errors(), report.toString());
  }

  @Test
  void anyInputReachesOurParser() throws CommandSyntaxException {
    Fixture fixture = new Fixture(rich());

    for (String input : List.of("rich", "rich give", "rich give bob 999", "rich give bob x:y", "rich mode абв",
                                "rich unknown words here", "rich list extra", "rich  give  bob", "rich secret", "rich hidden")) {
      fixture.executed.set(null);
      fixture.dispatcher.execute(input, fixture.sender);
      assertArrayEquals(words(input), fixture.executed.get(), input);
    }
  }

  @Test
  void aliasAndRedirectKeepArgumentsAfterTheLabel() throws CommandSyntaxException {
    Fixture fixture = new Fixture(rich());
    LiteralCommandNode<Sender> root = (LiteralCommandNode<Sender>) fixture.dispatcher.getRoot().getChild("rich");
    fixture.dispatcher.getRoot().addChild(copy("r", root));
    fixture.dispatcher.register(LiteralArgumentBuilder.<Sender>literal("execute")
                                  .then(LiteralArgumentBuilder.<Sender>literal("run").redirect(fixture.dispatcher.getRoot())));

    fixture.dispatcher.execute("r give bob 5", fixture.sender);
    assertArrayEquals(new String[]{"give", "bob", "5"}, fixture.executed.get());

    fixture.dispatcher.execute("execute run rich say hello world", fixture.sender);
    assertArrayEquals(new String[]{"say", "hello", "world"}, fixture.executed.get());

    List<String> suggestions = fixture.suggest("execute run rich mode ");
    assertTrue(suggestions.containsAll(List.of("ON", "OFF")), suggestions.toString());
  }

  @Test
  void validValuesFollowTypedNodes() {
    Fixture fixture = new Fixture(rich());

    for (String input : List.of("rich give bob 5", "rich speed 2.5", "rich mode ON true", "rich say hi there", "rich admin reload")) {
      assertFalse(fixture.reachesTail(input), input);
    }
    assertTrue(fixture.reachesTail("rich give bob x:y"));
  }

  @Test
  void deniedBranchesAreHiddenOnlyFromTheClient() {
    Fixture fixture = new Fixture(rich());
    LiteralCommandNode<Sender> root = fixture.root;
    TestSender sender = new TestSender("alice");

    assertTrue(root.getChild("hidden").canUse(sender), "server parsing must not be blocked");
    fixture.clientTree.set(true);
    assertFalse(root.getChild("hidden").canUse(sender));
    assertFalse(root.getChild("secret").canUse(sender));
    assertTrue(root.getChild("list").canUse(sender));
    assertNotNull(root.getChild("Список"));
  }

  @Test
  void hiddenLiteralsDoNotLeakIntoServerSuggestions() {
    Fixture fixture = new Fixture(rich());
    String input = "rich ";

    List<String> ours = fixture.completer.onTabComplete(fixture.sender, new String[]{""});
    Suggestions raw = fixture.dispatcher.getCompletionSuggestions(fixture.dispatcher.parse(input, fixture.sender)).join();
    List<String> kept = new ArrayList<>();
    BrigadierTree.keepOurs(input, raw, ours).getList().forEach(suggestion -> kept.add(suggestion.getText()));

    assertTrue(raw.getList().stream().anyMatch(suggestion -> suggestion.getText().equals("hidden")), "precondition of the test");
    assertFalse(kept.contains("hidden"));
    assertFalse(kept.contains("secret"));
    assertTrue(kept.containsAll(List.of("list", "give")), kept.toString());
  }

  @Test
  void siblingsWithOneNameButDifferentTypesStayApart() {
    CommandNode<Sender> player = new Fixture(rich()).root.getChild("give").getChild("player");

    List<String> names = new ArrayList<>();
    player.getChildren().forEach(child -> names.add(child.getName()));
    assertEquals(List.of("count", "count_2", BrigadierTree.TAIL), names);
  }

  private static String[] words(String input) {
    return Arrays.stream(input.split(" ")).filter(word -> !word.isEmpty()).skip(1).toArray(String[]::new);
  }

  private static LiteralCommandNode<Sender> copy(String alias, LiteralCommandNode<Sender> node) {
    LiteralCommandNode<Sender> copy = LiteralArgumentBuilder.<Sender>literal(alias).requires(node.getRequirement())
                                                            .executes(node.getCommand()).build();
    node.getChildren().forEach(copy::addChild);
    return copy;
  }

  static class Fixture {
    final CommandDispatcher<Sender> dispatcher = new CommandDispatcher<>();
    final AtomicReference<String[]> executed = new AtomicReference<>();
    final AtomicBoolean clientTree = new AtomicBoolean();
    final TestSender sender = new TestSender("alice");
    final TabCompleter completer;
    final LiteralCommandNode<Sender> root;

    Fixture(Command command) {
      completer = new TabCompleter(command) {};
      root = new BrigadierTree<Sender>(source -> source, (source, args) -> executed.set(args), completer::onTabComplete,
                                       source -> clientTree.get())
        .build(command, command.getName(), source -> true);
      dispatcher.getRoot().addChild(root);
    }

    List<String> suggest(String input) {
      List<String> texts = new ArrayList<>();
      dispatcher.getCompletionSuggestions(dispatcher.parse(input, sender)).join().getList()
                .forEach(suggestion -> texts.add(suggestion.getText()));
      return texts;
    }

    boolean reachesTail(String input) {
      return dispatcher.parse(input, sender).getContext().getLastChild().getNodes().stream()
                       .anyMatch(node -> BrigadierTree.TAIL.equals(node.getNode().getName()));
    }
  }
}
