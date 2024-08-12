package ru.cwcode.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.color.ColorGenerationStrategy;
import ru.cwcode.commands.color.ColoredScheme;
import ru.cwcode.commands.color.DefaultColorGenerationStrategy;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.permissions.DefaultPermissionGenerationStrategy;
import ru.cwcode.commands.permissions.PermissionGenerationStrategy;
import ru.cwcode.commands.permissions.ProcessResult;
import ru.cwcode.commands.preconditions.CommandPreconditionResult;
import ru.cwcode.commands.preconditions.Precondition;
import ru.cwcode.commands.preconditions.PredicatePrecondition;
import ru.cwcode.commands.preconditions.processor.PermissionPrecondition;
import ru.cwcode.commands.preconditions.processor.PreconditionProcessor;
import ru.cwcode.commands.preconditions.processor.PreconditionRequirements;
import ru.cwcode.commands.preconditions.processor.PreconditionResult;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class Command implements Permissible {
  protected List<ArgumentSet> argumentSets = new ArrayList<>();
  String name;
  ColorGenerationStrategy color = null;
  PermissionGenerationStrategy permissions = null;
  List<String> aliases = new ArrayList<>();
  boolean isSubcommand = false;
  String description = null;
  String permission;
  Deque<Precondition> preconditions = new ArrayDeque<>();
  Help help;
  Command parent = null;
  Command[] subcommands = new Command[]{};
  //присваивается только в рут-команде
  DebugMode debug = DebugMode.NONE;
  
  /**
   * Автоматически устанавливается пермишен name и устанавливаются алиасы
   */
  public Command(String name, List<String> aliases) {
    this(name);
    aliases(aliases);
  }
  
  /**
   * Автоматически устанавливается пермишен name
   */
  public Command(String name) {
    this.name = name;
    this.permission = name;
  }
  
  /**
   * Устанавливает алиасы для команды. Не работает для рут-команды. Переписывает текущие алиасы
   */
  public Command aliases(List<String> aliases) {
    this.aliases = aliases;
    return this;
  }
  
  /**
   * Шоткат для сингл-аргументсета в команде без аргументов
   */
  public Command(String name, AbstractExecutor executor) {
    this(name);
    arguments(new ArgumentSet(executor, name));
  }
  
  /**
   * Добавляет аргументсеты в команду или подкоманду
   */
  public Command arguments(ArgumentSet... arguments) {
    for (ArgumentSet set : arguments) {
      argumentSets.add(set);
      
      if (debug.is(DebugMode.DETAILED))
        debug.print(l10n.get("command.debug.registeredArgumentSet", name, set));
      
      if (set.optionalStart >= 0) {
        for (int i = set.arguments.length - 1; i >= set.optionalStart; i--) { //делает все возможные варианты без опциональных аргументов
          argumentSets.add(new ArgumentSet(set, Arrays.copyOfRange(set.arguments, 0, i)).hidden());
          
          if (debug.is(DebugMode.DETAILED)) debug.print(l10n.get("command.debug.optionalAdapt", set));
        }
      }
    }
    
    return this;
  }
  
  /**
   * Шоткат для сингл-аргументсета в команде без аргументов с кастомным пермишеном
   */
  public Command(String name, String permission, AbstractExecutor executor) {
    this(name, permission);
    arguments(new ArgumentSet(executor, name));
  }
  
  /**
   * Указывайте пермишен "" для того, чтобы пермишены рекурсивно не генерировались
   */
  public Command(String name, String permission) {
    this(name);
    this.permission = permission;
  }
  
  /**
   * Шоткат для сингл-аргументсета в команде с любыми аргументами
   */
  public Command(String name, AbstractExecutor executor, Argument... arguments) {
    this(name);
    arguments(new ArgumentSet(executor, "", arguments));
  }
  
  @Deprecated(forRemoval = true)
  public Command setIgnoreExecutionPossibility(boolean ignoreExecutionPossibility) {
    return this;
  }
  
  /**
   * Устанавливает алиасы для команды. Не работает для рут-команды. Переписывает текущие алиасы
   */
  public Command aliases(String... aliases) {
    this.aliases = List.of(aliases);
    return this;
  }
  
  /**
   * Возвращает алиасы команды
   */
  public List<String> aliases() {
    return this.aliases;
  }
  
  /**
   * Добавляет подкоманды в команду. Можно использовать несколько раз
   */
  public Command subCommands(Command... subcommands) {
    this.subcommands = subcommands;
    for (Command subcommand : subcommands) {
      subcommand.isSubcommand = true;
      subcommand.parent = this;
      subcommand.debug = debug;
      
      if (debug.is(DebugMode.DETAILED))
        debug.print(l10n.get("command.debug.registeredSubcommand", getName(), subcommand.getName()));
    }
    
    return this;
  }
  
  /**
   * Регистрирует команду. Вызывать только раз.
   */
  public void register() {
    
    if (isSubcommand) return;
    if (debug != DebugMode.NONE) updateDebug(debug);
    if (permissions == null) permissions = new DefaultPermissionGenerationStrategy();
    if (color == null) color = DefaultColorGenerationStrategy.INSTANCE;
    
    this.name = this.name.toLowerCase();
    
    updatePermissions(permission);
    
    CommandsAPI.getPlatform().registerCommand(this);
  }
  
  /**
   * Очищает аргументсеты из команды или подкоманды
   */
  public void clearArgumentSets() {
    this.argumentSets.clear();
  }
  
  /**
   * Устанавливает свой хелп вместо авто-генерируемого
   */
  public Command help(Help help) {
    this.help = help;
    return this;
  }
  
  /**
   * Устанавливает краткое описание команды для автогенерируемого хелпа
   */
  public Command description(String shortDescription) {
    this.description = shortDescription;
    return this;
  }
  
  public String description() {
    return description;
  }
  
  public Command debug(DebugMode mode) {
    this.debug = mode;
    return this;
  }
  
  public Command getRootCommand() {
    if (isSubcommand) return parent.getRootCommand();
    return this;
  }
  
  public ColorGenerationStrategy getColorScheme() {
    if (color == null) return parent.getColorScheme();
    return color;
  }
  
  /**
   * Устанавливает цветовое оформление для хелпа и других сообщений.
   */
  public Command setColorScheme(ColorGenerationStrategy colorGenerationStrategy) {
    this.color = colorGenerationStrategy;
    return this;
  }
  
  public Command setColorScheme(TextColor color) {
    setColorScheme(new ColoredScheme(color));
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  /**
   * Устанавливает новое имя для команды, если это подкоманда
   */
  public Command setName(String name) {
    if (this.isSubcommand) {
      this.name = name;
    }
    return this;
  }
  
  @Override
  public String getPermission() {
    return permission;
  }
  
  PermissionGenerationStrategy getPermissions() {
    if (permissions == null) {
      if (parent == null) return null;
      return parent.getPermissions();
    }
    return permissions;
  }
  
  /**
   * @param strategy стратегия генерации пермишенов
   *                 Использовать только в рут-команде
   */
  public Command setPermissions(PermissionGenerationStrategy strategy) {
    this.permissions = strategy;
    return this;
  }
  
  /**
   * Предикат, который проверяется при автокомплите, выводе хелпа и попытке выполнения
   */
  public Command canExecute(Predicate<Sender> canExecute) {
    this.preconditions.add(new PredicatePrecondition(canExecute));
    return this;
  }
  
  
  /**
   * Добавляет Precondition`s в конец списка
   */
  public Command preconditions(Precondition... preconditions) {
    this.preconditions.addAll(Arrays.asList(preconditions));
    return this;
  }
  
  protected void updatePermissions(String permissions) {
    ProcessResult result;
    
    if (isSubcommand) {
      result = getPermissions().processSubCommand(permissions, permission, name);
      
      if (debug.is(DebugMode.DETAILED))
        debug.print(l10n.get("command.debug.subcommandPermissionSet", name, result.getPermission()));
    } else {
      result = getPermissions().processCommand(permission, name);
      
      if (debug.is(DebugMode.DETAILED))
        debug.print(l10n.get("command.debug.commandPermissionSet", name, result.getPermission()));
    }
    
    permissions = result.getNextPermissions();
    permission = result.getPermission();
    updatePermissionPrecondition();
    
    for (Command subcommand : subcommands) {
      subcommand.updatePermissions(permissions);
    }
    
    for (ArgumentSet argumentSet : argumentSets) {
      argumentSet.permission = getPermissions().processArgumentSet(permissions, argumentSet.permission, permission);
      argumentSet.updatePermissionPrecondition();
      
      if (debug.is(DebugMode.DETAILED))
        debug.print(l10n.get("command.debug.argumentSetPermissionSet", name + argumentSet, argumentSet.permission));
    }
  }
  
  private void updatePermissionPrecondition() {
    for (Precondition precondition : preconditions) {
      if (precondition instanceof PermissionPrecondition) {
        return;
      }
    }
    
    preconditions.addFirst(new PermissionPrecondition(this));
  }
  
  protected void onExecute(Sender sender, String[] args, ArgumentSet founded) {
    var start = System.nanoTime();
    
    founded.execute(sender, args, this);
    
    if (debug.is(DebugMode.REDUCED))
      debug.print(l10n.get("command.debug.executionTook", founded, (System.nanoTime() - start), (System.nanoTime() - start) / 1000000));
  }
  
  protected List<Command> getSubcommandsFor(Sender sender, PreconditionRequirements requirements) {
    List<Command> list = new ArrayList<>();
    
    for (Command command : subcommands) {
      if (command.checkPreconditions(sender, requirements)) {
        list.add(command);
      }
    }
    
    return list;
  }
  
  protected boolean canPerformedBy(Sender sender) {
    return checkPreconditions(sender).getResult().canPerform();
  }
  
  protected CommandPreconditionResult checkPreconditions(Sender sender) {
    return new CommandPreconditionResult(this, PreconditionProcessor.process(sender, preconditions));
  }
  
  protected boolean checkPreconditions(Sender sender, PreconditionRequirements requirements) {
    return PreconditionProcessor.process(sender, preconditions).isSatisfy(requirements);
  }
  
  protected Command getSubcommandFor(String arg, Sender sender) {
    
    for (Command command : subcommands) {
      if ((command.name.equalsIgnoreCase(arg) || command.aliases.contains(arg))
          && command.checkPreconditions(sender, PreconditionRequirements.CAN_PERFORM_AND_CAN_SEE)) {
        return command;
      }
    }
    
    return null;
  }
  
  
  protected List<ArgumentSet> getArgumentSetsFor(Sender sender, PreconditionRequirements requirements) {
    List<ArgumentSet> list = new ArrayList<>();
    
    for (ArgumentSet arg : argumentSets) {
      if (arg.checkPreconditions(sender, requirements)) {
        list.add(arg);
      }
    }
    return list;
  }
  
  protected ArgumentSearchResult searchForArgumentSet(Sender sender, String... args) {
    ArgumentSearchResult result = new ArgumentSearchResult();
    
    for (ArgumentSet set : argumentSets) {
      PreconditionResult preconditionResult = set.checkPreconditions(sender);
      
      ArgumentFitnessResult fitnessResult = set.isArgumentsFit(sender, args);
      
      String errorMessage = null;
      if (!preconditionResult.canPerform()) {
        if (preconditionResult.getCannotPerformMessage() != null) {
          errorMessage = preconditionResult.getCannotPerformMessage();
        } else continue;
      }
      
      if (fitnessResult.success()) {
        if (errorMessage == null) {
          return result.founded(set);
        } else {
          return result.error(set, errorMessage);
        }
      } else {
        if (preconditionResult.canSee()) result.add(fitnessResult);
      }
    }
    
    return result;
  }
  
  protected void onError(Sender sender, String label, String[] args, ArgumentSearchResult argumentSearchResult) {
    if (argumentSearchResult.getErrorMessage() != null) {
      showErrorMessage(sender, label, argumentSearchResult);
    } else if (argumentSearchResult.canShowDetailedHelp()) {
      showDetailedHelp(sender, label, argumentSearchResult);
    } else {
      showFullHelp(sender, label, args);
    }
  }
  
  private void showErrorMessage(Sender sender, String label, ArgumentSearchResult argumentSearchResult) {
    List<Component> toSend = new ArrayList<>();
    
    toSend.add(Component.empty());
    
    toSend.add(argumentSearchResult.getFounded().toComponent(sender, getColorScheme()));
    toSend.add(Component.text("↳ ")
                        .append(Component.text(argumentSearchResult.getErrorMessage()))
                        .color(getColorScheme().accent(true)));
    
    toSend.add(Component.empty());
    
    for (Component line : toSend) {
      sender.sendMessage(line);
    }
  }
  
  protected void showFullHelp(Sender sender, String label, String[] args) {
    if (help == null) {
      sendAutoHelp(sender, label, args);
    } else {
      help.sendTo(sender, this, label, args);
    }
  }
  
  private void updateDebug(DebugMode debug) {
    this.debug = debug;
    
    for (Command subcommand : subcommands) {
      subcommand.updateDebug(debug);
    }
  }
  
  private void showDetailedHelp(Sender sender, String label, ArgumentSearchResult argumentSearchResult) {
    Component written = Component.text("  /" + getFullCommandPath(label), getColorScheme().written(true));
    List<Component> toSend = new ArrayList<>();
    
    toSend.add(Component.empty());
    
    for (ArgumentFitnessResult invalidResult : argumentSearchResult.getInvalidResults()) {
      toSend.add(written.append(invalidResult.getArgumentSet().toComponent(sender, getColorScheme())));
      toSend.add(Component.text("↳ ")
                          .append(invalidResult.getInvalidArgument().invalidMessage(this, sender, invalidResult.getInvalidStringArgument()))
                          .color(getColorScheme().accent(true)));
      toSend.add(Component.empty());
    }
    
    for (Component row : toSend) {
      sender.sendMessage(row);
    }
  }
  
  private void sendAutoHelp(Sender sender, String label, String[] args) {
    ColorGenerationStrategy color = getColorScheme();
    
    Component written = Component.text("  /" + getFullCommandPath(label));
    
    List<Component> toSend = new ArrayList<>();
    
    for (Command subcommand : getSubcommandsFor(sender, PreconditionRequirements.ONLY_CAN_SEE)) {
      boolean canPerformedBy = subcommand.canPerformedBy(sender);
      
      toSend.add(written.color(color.written(canPerformedBy))
                        .append(Component.text(" " + subcommand.name + " ", color.subcommand(canPerformedBy)))
                        .append(sender.isOp() ? Component.text(subcommand.permission, color.permissions(canPerformedBy)) : Component.empty())
      );
    }
    
    boolean previousWasEmptyLine = false;
    
    for (ArgumentSet argumentSet : filterArgumentSets(getArgumentSetsFor(sender, PreconditionRequirements.ONLY_CAN_SEE), args)) {
      
      if (argumentSet.isHidden()) continue;
      
      boolean canPerformedBy = argumentSet.canPerformedBy(sender);
      boolean hasHelp = argumentSet.hasHelp();
      
      if (!previousWasEmptyLine && hasHelp) toSend.add(Component.empty());
      
      toSend.add(written.color(color.written(canPerformedBy))
                        .append(argumentSet.toComponent(sender, color)));
      
      if (hasHelp) {
        toSend.add(Component.text("↳ ")
                            .append(argumentSet.help)
                            .color(color.accent(canPerformedBy)));
        
        toSend.add(Component.empty());
        previousWasEmptyLine = true;
      } else {
        previousWasEmptyLine = false;
      }
    }
    
    sendDescription(sender, color);
    
    sender.sendMessage("");
    
    if (toSend.isEmpty()) {
      sender.sendMessage(Component.text(l10n.get("command.noContinuation"), color.main()));
    } else {
      sender.sendMessage(Component.text(l10n.get("command.continuations"), color.main()));
      sender.sendMessage("");
      
      for (Component row : toSend) {
        sender.sendMessage(row);
      }
    }
  }
  
  private List<ArgumentSet> filterArgumentSets(List<ArgumentSet> argumentSets, String[] args) {
    if (args.length == 0) return argumentSets;
    
    List<ArgumentSet> relevantArgumentSets = argumentSets.stream().filter(x -> x.shouldShowInHelp(List.of(args))).collect(Collectors.toList());
    
    if (relevantArgumentSets.isEmpty()) return argumentSets;
    
    return relevantArgumentSets;
  }
  
  private void sendDescription(Sender sender, ColorGenerationStrategy color) {
    if (description != null) {
      sender.sendMessage("");
      sender.sendMessage(Component.text(l10n.get("command.description"), color.main()));
      
      boolean firstLine = true;
      
      for (String part : description.split("\n")) {
        sender.sendMessage(Component.text((firstLine ? "↳ " : "  ") + part, color.accent(true)));
        firstLine = false;
      }
    }
  }
  
  @NotNull
  public String getFullCommandPath(String label) {
    StringBuilder writtenString = new StringBuilder();
    
    Command rootCommand = this;
    
    while (rootCommand.isSubcommand) {
      writtenString.insert(0, " " + rootCommand.name);
      rootCommand = rootCommand.parent;
    }
    
    writtenString.insert(0, label);
    return writtenString.toString();
  }
  
  private int getCommandPathLength() {
    int length = 0;
    
    Command parent = this;
    
    while (parent != null) {
      length++;
      parent = parent.parent;
    }
    
    return length;
  }
}
