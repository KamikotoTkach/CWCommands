package ru.cwcode.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.arguments.basic.IntegerArg;
import ru.cwcode.commands.arguments.basic.StringArg;
import ru.cwcode.commands.arguments.spaced.SpacedStringArg;
import ru.cwcode.commands.executor.AbstractAutowiredExecutor;
import ru.cwcode.commands.preconditions.Precondition;
import ru.cwcode.commands.test.L10PlatformTest;
import ru.cwcode.commands.test.TestConfigPlatform;
import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.cwutils.l10n.L10n;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class CommandRuntimeTest {
  static final Queue<String> calls = new ConcurrentLinkedQueue<>();
  static final List<Supplier<String>> continuations = new CopyOnWriteArrayList<>();
  static volatile CyclicBarrier barrier;

  @BeforeAll
  static void before() {
    L10PlatformTest l10PlatformTest = new L10PlatformTest();
    CommandsAPI.setPlatform(new TestConfigPlatform());
    CommandsAPI.setL10n(new L10n(l10PlatformTest));
    CommandsAPI.setConfig(new SimpleConfig("config", l10PlatformTest));
  }

  @BeforeEach
  void reset() {
    calls.clear();
    continuations.clear();
    barrier = null;
  }

  @Test
  void parallelInvocationsKeepTheirOwnSenderAndArguments() throws Exception {
    Command command = registered(new Command("parallel").arguments(new ArgumentSet(new Recorder(), new StringArg("value"))));
    barrier = new CyclicBarrier(2);

    ExecutorService pool = Executors.newFixedThreadPool(2);
    try {
      Future<?> first = pool.submit(() -> run(command, new TestSender("alice"), "a"));
      Future<?> second = pool.submit(() -> run(command, new TestSender("bob"), "b"));
      first.get(5, TimeUnit.SECONDS);
      second.get(5, TimeUnit.SECONDS);
    } finally {
      pool.shutdownNow();
    }

    assertEquals(Set.of("alice:a", "bob:b"), new HashSet<>(calls));
  }

  @Test
  void senderStaysAvailableAfterTheMethodReturns() {
    Command command = registered(new Command("later").arguments(new ArgumentSet(new Deferred())));

    run(command, new TestSender("alice"));

    assertEquals(1, continuations.size());
    assertEquals("alice", continuations.get(0).get());
  }

  @Test
  void omittedOptionalArgumentGetsItsDefaultValue() {
    Command command = registered(new Command("defaults")
                                   .arguments(new ArgumentSet(new Pair(), new StringArg("a"), new StringArg("b").optional("fallback"))));

    run(command, new TestSender("alice"), "x");
    run(command, new TestSender("alice"), "x", "y");

    assertEquals(List.of("x,fallback", "x,y"), new ArrayList<>(calls));
  }

  @Test
  void shortenedVariantDoesNotSwallowWordsIntoItsLastArgument() {
    Command command = registered(new Command("spaced")
                                   .arguments(new ArgumentSet(new Recorder(), new StringArg("value"),
                                                              new IntegerArg("count").optional(),
                                                              new SpacedStringArg("text").optional())));

    run(command, new TestSender("alice"), "x", "notANumber", "more");

    assertTrue(calls.isEmpty(), "the variant without optionals must not accept three words: " + calls);
  }

  @Test
  void subCommandsCanBeAddedSeveralTimes() {
    Command command = registered(new Command("root")
                                   .subCommands(new Command("first", new Recorder.NoArgs()))
                                   .subCommands(new Command("second", new Recorder.NoArgs())));

    TestSender sender = new TestSender("alice");
    assertEquals("first", CommandParser.parse(command, sender, "first").getCommand().getName());
    assertEquals("second", CommandParser.parse(command, sender, "second").getCommand().getName());
  }

  @Test
  void subCommandAliasIgnoresCase() {
    Command command = registered(new Command("aliases")
                                   .subCommands(new Command("list", new Recorder.NoArgs()).aliases("ls")));

    assertEquals("list", CommandParser.parse(command, new TestSender("alice"), "LS").getCommand().getName());
  }

  @Test
  void deniedSetDoesNotHideAnAllowedSetWithTheSameArguments() {
    Command command = registered(new Command("denied")
                                   .arguments(new ArgumentSet(new Recorder.Named("denied"), new StringArg("value")).preconditions(new Deny()),
                                              new ArgumentSet(new Recorder.Named("allowed"), new StringArg("value"))));

    TestSender sender = new TestSender("alice");
    run(command, sender, "x");

    assertEquals(List.of("allowed:x"), new ArrayList<>(calls));
    assertTrue(sender.messages.isEmpty(), "no refusal expected: " + sender.messages);
  }

  @Test
  void refusalIsShownWhenOnlyTheDeniedSetFits() {
    Command command = registered(new Command("refused")
                                   .arguments(new ArgumentSet(new Recorder.Named("denied"), new StringArg("value")).preconditions(new Deny()))
                                   .help((sender, args) -> sender.sendMessage("custom help")));

    TestSender sender = new TestSender("alice");
    run(command, sender, "x");

    assertTrue(calls.isEmpty());
    assertTrue(sender.messages.stream().anyMatch(message -> message.contains(Deny.MESSAGE)), sender.messages.toString());
  }

  @Test
  void integerMaxIsInclusive() {
    IntegerArg rarity = new IntegerArg("rarity").setMin(1).setMax(5);

    assertTrue(rarity.valid("5"));
    assertFalse(rarity.valid("6"));
  }

  @Test
  void enumArgIgnoresCase() {
    EnumArg arg = new EnumArg(TimeUnit.values(), "unit");

    assertTrue(arg.valid("seconds"));
    assertEquals(TimeUnit.SECONDS, ((Argument) arg).withRaw("Seconds").map());
    assertFalse(arg.valid("fortnights"));
  }

  @Test
  void nullMappedValueReachesAReferenceParameter() {
    Command command = registered(new Command("nulls").arguments(new ArgumentSet(new Recorder(), new NullArg())));

    run(command, new TestSender("alice"), "anything");

    assertEquals(List.of("alice:null"), new ArrayList<>(calls));
  }

  @Test
  void executorReportsMethodsWithTheSameArguments() {
    Ambiguous executor = new Ambiguous();

    assertEquals(1, executor.definitionProblems().size(), executor.definitionProblems().toString());
    assertTrue(executor.acceptsArgumentCount(0));
    assertFalse(executor.acceptsArgumentCount(1));
  }

  @Test
  void exactStringArgumentsAreNotCountedForTheMethod() {
    ArgumentSet set = new ArgumentSet(new Recorder(), new ExactStringArg("set"), new StringArg("value"));

    assertEquals(1, set.mappedArgumentCount());
    assertTrue(set.executor().acceptsArgumentCount(set.mappedArgumentCount()));
  }

  @Test
  void senderIsInjectedIntoTheFirstParameter() {
    Command command = registered(new Command("injected").arguments(new ArgumentSet(new Injected(), new StringArg("value"))));

    run(command, new TestSender("alice"), "x");

    assertEquals(List.of("injected alice:x"), new ArrayList<>(calls));
  }

  @Test
  void fullSignatureWinsOverSenderInjection() {
    Command command = registered(new Command("target").arguments(new ArgumentSet(new Injected(), new SenderArg(), new StringArg("value"))));

    run(command, new TestSender("alice"), "bob", "x");

    assertEquals(List.of("explicit bob:x"), new ArrayList<>(calls));
  }

  @Test
  void paddedFullSignatureWinsOverSenderInjection() {
    Command command = registered(new Command("padded").arguments(new ArgumentSet(new TargetWithOptional(), new SenderArg(), new SenderArg().optional())));

    run(command, new TestSender("alice"), "bob");

    assertEquals(List.of("bob,Optional.empty"), new ArrayList<>(calls));
  }

  @Test
  void playerOnlyMethodRefusesNonPlayers() {
    Command command = registered(new Command("playeronly").arguments(new ArgumentSet(new PlayerOnly())));

    TestSender console = new TestSender("console", false);
    run(command, console);
    run(command, new TestSender("alice"));

    assertEquals(List.of("alice"), new ArrayList<>(calls));
    assertFalse(console.messages.isEmpty());
  }

  @Test
  void omittedOptionalArgumentsArriveAsNullAndEmptyOptional() {
    Command command = registered(new Command("optionals")
                                   .arguments(new ArgumentSet(new WithOptionals(), new StringArg("a"),
                                                              new StringArg("b").optional(),
                                                              new IntegerArg("n").optional())));

    run(command, new TestSender("alice"), "x");
    run(command, new TestSender("alice"), "x", "y");
    run(command, new TestSender("alice"), "x", "y", "3");

    assertEquals(List.of("x,null,Optional.empty", "x,y,Optional.empty", "x,y,Optional[3]"), new ArrayList<>(calls));
  }

  @Test
  void exactOverloadWinsOverNullPadding() {
    Command command = registered(new Command("overload")
                                   .arguments(new ArgumentSet(new Overloaded(), new StringArg("a"), new StringArg("b").optional())));

    run(command, new TestSender("alice"), "x");

    assertEquals(List.of("one:x"), new ArrayList<>(calls));
  }

  @Test
  void onlyPublicInstanceMethodsAreDispatched() {
    WithHelpers executor = new WithHelpers();

    assertTrue(executor.acceptsArgumentCount(0));
    assertFalse(executor.acceptsArgumentCount(1));
    assertTrue(executor.definitionProblems().isEmpty(), executor.definitionProblems().toString());
  }

  @Test
  void requireConfirmAsksForTheWordAndThenExecutes() {
    Command command = registered(new Command("confirmed")
                                   .arguments(new ArgumentSet(new Pair(), new StringArg("a"), new StringArg("b").optional()).requireConfirm()));

    TestSender sender = new TestSender("console", false);
    run(command, sender, "x");

    assertTrue(calls.isEmpty());
    assertTrue(sender.messages.stream().anyMatch(message -> message.contains("/confirmed x confirm")), sender.messages.toString());

    run(command, sender, "x", "confirm");
    run(command, sender, "x", "y", "confirm");

    assertEquals(List.of("x,null", "x,y"), new ArrayList<>(calls));
  }

  private static Command registered(Command command) {
    command.register();
    return command;
  }

  private static void run(Command command, Sender sender, String... args) {
    new CommandParser(command) {}.onCommandExecute(sender, command.getName(), args);
  }

  public static class Recorder extends TestExecutor {
    public void run(String value) {
      awaitBarrier();
      calls.add(sender.getName() + ":" + value);
    }

    public static class NoArgs extends TestExecutor {
      public void run() {
        calls.add(sender.getName());
      }
    }

    public static class Named extends TestExecutor {
      private final String name;

      Named(String name) {
        this.name = name;
      }

      public void run(String value) {
        calls.add(name + ":" + value);
      }
    }
  }

  public static class Pair extends TestExecutor {
    public void run(String a, String b) {
      calls.add(a + "," + b);
    }
  }

  public static class Deferred extends TestExecutor {
    public void run() {
      continuations.add(() -> sender.getName());
    }
  }

  public static class Ambiguous extends TestExecutor {
    public void start() {
    }

    public void stop() {
    }
  }

  public static class Injected extends TestExecutor {
    public void run(Sender sender, String value) {
      calls.add("injected " + sender.getName() + ":" + value);
    }

    public void give(TestSender target, String value) {
      calls.add("explicit " + target.getName() + ":" + value);
    }
  }

  public static class TargetWithOptional extends TestExecutor {
    public void run(TestSender target, Optional<TestSender> other) {
      calls.add(target.getName() + "," + other.map(TestSender::getName));
    }
  }

  public static class PlayerOnly extends TestExecutor {
    public void run(TestSender player) {
      calls.add(player.getName());
    }
  }

  public static class WithOptionals extends TestExecutor {
    public void run(String a, String b, Optional<Integer> n) {
      calls.add(a + "," + b + "," + n);
    }
  }

  public static class Overloaded extends TestExecutor {
    public void one(String a) {
      calls.add("one:" + a);
    }

    public void two(String a, String b) {
      calls.add("two:" + a + "," + b);
    }
  }

  public static class WithHelpers extends TestExecutor {
    public void run() {
    }

    private void helper(String value) {
    }

    void packagePrivate(String value) {
    }

    public static void utility(String value) {
    }
  }

  abstract static class TestExecutor extends AbstractAutowiredExecutor {
    /**
     * {@link TestSender} играет роль платформенного игрока.
     */
    @Override
    protected SenderParameter senderParameter(Class<?> type) {
      return type == TestSender.class ? SenderParameter.PLAYER : super.senderParameter(type);
    }

    @Override
    protected Object senderValue(Class<?> type) {
      return type == TestSender.class ? sender : super.senderValue(type);
    }

    @Override
    public void handleError(Exception exception) {
      if (exception instanceof MessageReturn) {
        sender.sendMessage(exception.getMessage());
        return;
      }
      throw new RuntimeException(exception);
    }

    @Override
    protected Audience sender() {
      return Audience.empty();
    }

    @Override
    protected Optional<? extends Audience> argP(int index) {
      return Optional.empty();
    }

    static void awaitBarrier() {
      CyclicBarrier current = barrier;
      if (current == null) return;
      try {
        current.await(5, TimeUnit.SECONDS);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
  }

  static class NullArg extends Argument {
    @Override
    public boolean valid(String raw) {
      return true;
    }

    @Override
    public Collection<String> completions(Sender sender) {
      return List.of();
    }

    @Override
    public String argumentName() {
      return "nothing";
    }

    @Override
    public Object map() {
      return null;
    }
  }

  static class SenderArg extends Argument {
    @Override
    public boolean valid(String raw) {
      return true;
    }

    @Override
    public Collection<String> completions(Sender sender) {
      return List.of();
    }

    @Override
    public String argumentName() {
      return "target";
    }

    @Override
    public Object map() {
      return new TestSender(raw);
    }
  }

  static class Deny implements Precondition {
    static final String MESSAGE = "not for you";

    @Override
    public boolean canExecute(Sender sender) {
      return false;
    }

    @Override
    public String cannotExecuteFeedback(Sender sender) {
      return MESSAGE;
    }
  }

  public static class TestSender implements Sender {
    final String name;
    final boolean player;
    final List<String> messages = new CopyOnWriteArrayList<>();

    TestSender(String name) {
      this(name, true);
    }

    TestSender(String name, boolean player) {
      this.name = name;
      this.player = player;
    }

    @Override
    public boolean hasPermission(String str) {
      return true;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void sendMessage(Component line) {
      messages.add(String.valueOf(line));
    }

    @Override
    public boolean isPlayer() {
      return player;
    }

    @Override
    public void sendMessage(TargetableMessageReturn targetable) {
    }

    @Override
    public void sendMessage(String message) {
      messages.add(message);
    }

    @Override
    public Audience getAudience() {
      return Audience.empty();
    }

    @Override
    public void confirm(String confirmableString, long timeToConfirm, Runnable onConfirm, Runnable onExpired) {
      onConfirm.run();
    }
  }
}
