package tkachgeek.commands.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tkachgeek.commands.command.arguments.executor.Executable;
import tkachgeek.commands.command.arguments.executor.Executor;
import tkachgeek.commands.command.permissions.DefaultPermissionGenerationStrategy;
import tkachgeek.commands.command.permissions.PermissionGenerationStrategy;
import tkachgeek.commands.command.permissions.ProcessResult;
import tkachgeek.tkachutils.messages.MessagesUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {
  static TextColor text = TextColor.fromHexString("#00a6f0");
  static TextColor argument = TextColor.fromHexString("#00baff");
  static TextColor argumentOptional = TextColor.fromHexString("#02d7ff");
  static TextColor subcommandColor = TextColor.fromHexString("#0098dc");
  static TextColor writtenColor = TextColor.fromHexString("#007ab5");
  static TextColor permissionColor = TextColor.fromHexString("#055080");
  static TextColor comment = TextColor.fromHexString("#8adaff");
  
  final String name;
  protected List<ArgumentSet> argumentSets = new ArrayList<>();
  List<String> aliases = new ArrayList<>();
  boolean isSubcommand = false;
  String description = "";
  String permission = "";
  Help help;
  Command parent = null;
  Command[] subcommands = new Command[]{};
  //присваивается только в рут-команде
  PermissionGenerationStrategy strategy = null;
  JavaPlugin plugin;
  
  DebugMode debug = DebugMode.NONE;
  
  /**
   * Автоматически устанавливается пермишен name
   */
  public Command(String name) {
    this.name = name;
  }
  
  /**
   * Автоматически устанавливается пермишен name и устанавливаются алиасы
   */
  public Command(String name, List<String> aliases) {
    this(name);
    aliases(aliases);
  }
  
  /**
   * Указывайте пермишен "" для того, чтобы пермишены рекурсивно не генерировались
   */
  public Command(String name, String permission) {
    this(name);
    this.permission = permission;
  }
  
  /**
   * Шоткат для сингл-аргументсета в команде без аргументов с кастомным пермишеном
   */
  public Command(String name, String permission, Executable executor) {
    this(name, permission);
    arguments(new ArgumentSet(executor, name));
  }
  /**
   * Шоткат для сингл-аргументсета в команде без аргументов с кастомным пермишеном
   */
  public Command(String name, String permission, Executor executor) {
    this(name, permission, (Executable) executor);
  }
  
  /**
   * Шоткат для сингл-аргументсета в команде без аргументов
   */
  public Command(String name, Executable executor) {
    this(name, name);
    arguments(new ArgumentSet(executor, name));
  }
  /**
   * Шоткат для сингл-аргументсета в команде без аргументов
   */
  public Command(String name, Executor executor) {
    this(name, (Executable) executor);
  }
  
  /**
   * Шоткат для сингл-аргументсета в команде с любыми аргументами
   */
  public Command(String name, Executable executor, Argument... arguments) {
    this(name);
    arguments(new ArgumentSet(executor, name, arguments));
  }
  /**
   * Шоткат для сингл-аргументсета в команде с любыми аргументами
   */
  public Command(String name, Executor executor, Argument... arguments) {
    this(name, (Executable) executor, arguments);
  }
  
  /**
   * Устанавливает цветовое оформление для хелпа и других сообщений. Статична, нужно переписать
   */
  public static void setColorScheme(TextColor text, TextColor argument, TextColor argumentOptional, TextColor subcommandColor, TextColor writtenColor, TextColor permissionColor, TextColor comment) {
    Command.argumentOptional = argumentOptional;
    Command.permissionColor = permissionColor;
    Command.subcommandColor = subcommandColor;
    Command.writtenColor = writtenColor;
    Command.argument = argument;
    Command.comment = comment;
    Command.text = text;
  } //todo: вынести из статика, сделать генерацию суб-цветов из основного
  
  /**
   * Устанавливает алиасы для команды. Не работает для рут-команды. Переписывает текущие алиасы
   */
  public Command aliases(List<String> aliases) {
    this.aliases = aliases;
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
   * Добавляет подкоманды в команду. Можно использовать несколько раз
   */
  public Command subCommands(Command... subcommands) {
    this.subcommands = subcommands;
    for (Command subcommand : subcommands) {
      subcommand.isSubcommand = true;
      subcommand.parent = this;
    }
    
    return this;
  }
  
  /**
   * @param strategy стратегия генерации пермишенов
   *                 Использовать только в рут-команде
   */
  public Command setPermissionGenerationStrategy(PermissionGenerationStrategy strategy) {
    this.strategy = strategy;
    return this;
  }
  
  /**
   * Регистрирует команду. Вызывать только раз.
   */
  public void register(JavaPlugin plugin) {
    this.plugin = plugin;
    
    if (isSubcommand) return;
    if (strategy == null) strategy = new DefaultPermissionGenerationStrategy();
    
    updatePermissions(permission);
    
    try {
      plugin.getCommand(name).setTabCompleter(new TabCompleter(this));
      plugin.getCommand(name).setExecutor(new CommandParser(this));
      
      if (!description.isEmpty()) plugin.getCommand(name).setDescription(description);//может быть не работает, как и алиасы

      if (!permission.isEmpty())
        plugin.getCommand(name).setPermission(strategy.processCommand(permission, name).getPermission());
    } catch (Exception e) {
      Bukkit.getLogger().warning("Не удалось зарегистрировать команду " + name + " ввиду её отсутствия в plugin.yml");
    }
  }
  

  
  /**
   * Добавляет аргументсеты в команду или подкоманду
   */
  public Command arguments(ArgumentSet... arguments) {
    this.argumentSets.addAll(List.of(arguments));
    
    for (ArgumentSet set : arguments) {
      if (set.optionalStart > 0) {
        for (int i = set.arguments.length - 1; i >= set.optionalStart; i--) { //делает все возможные варианты без опциональных аргументов
          ArgumentSet newArgumentSet = new ArgumentSet(set.executor, set.permission, Arrays.copyOfRange(set.arguments, 0, i));
          newArgumentSet.spacedLastArgument = set.spacedLastArgument;
          newArgumentSet.help = set.help;
          newArgumentSet.blockForNonPlayers = set.blockForNonPlayers;
          newArgumentSet.blockForPlayers = set.blockForPlayers;
          argumentSets.add(newArgumentSet);
        }
      }
    }
    
    return this;
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
  
  public Command debug(DebugMode mode) {
    this.debug = mode;
    return this;
  }
  
  protected void updatePermissions(String permissions) {
    ProcessResult result;
    
    if (isSubcommand) {
      result = getStrategy().processSubCommand(permissions, permission, name);
      
      if (debug.is(DebugMode.DETAILED))
        debug.print("Подкоманде " + name + " установлены права " + result.getPermission() + "");
    } else {
      result = getStrategy().processCommand(permission, name);
      
      if (debug.is(DebugMode.DETAILED))
        debug.print("Команде " + name + " установлены права " + result.getPermission() + "");
    }
    
    permissions = result.getNextPermissions();
    permission = result.getPermission();
    
    for (Command subcommand : subcommands) {
      subcommand.updatePermissions(permissions);
    }
    
    for (ArgumentSet argumentSet : argumentSets) {
      argumentSet.permission = getStrategy().processArgumentSet(permissions, argumentSet.permission, permission);
      
      if (debug.is(DebugMode.DETAILED))
        debug.print("Аргументсету " + permissions + "/" + argumentSet + " установлены права " + argumentSet.permission);
    }
  }
  
  protected void onError(CommandSender sender, String[] args) {
    helpFor(sender, args);
  }
  
  protected boolean canPerformedBy(CommandSender sender) {
    boolean result = permission != null && (sender.hasPermission(permission) || permission.isEmpty()) || sender.isOp();
    
    if (debug.is(DebugMode.DETAILED))
      debug.print("Проверка " + sender.getName() + " на возможность выполнения " + this.name + ": " + (result ? " успешно" : "провал"));
    return result;
  }
  
  protected void onExecute(CommandSender sender, String[] args) {
    
    if (debug.is(DebugMode.DETAILED))
      debug.print("Попытка выполнения " + this.name + " с " + Arrays.toString(args) + " для " + sender.getName());
    
    for (ArgumentSet set : argumentSets) {
      if (set.isArgumentsFit(args) && set.canPerformedBy(sender)) {
        
        if (debug.is(DebugMode.MAIN)) debug.print("Выполнение " + set);
        
        var start = System.nanoTime();
        set.execute(sender, args, this);
        if (debug.is(DebugMode.MAIN))
          debug.print("Выполнение " + set + " заняло " + (System.nanoTime() - start) + "ns (" + (System.nanoTime() - start) / 1000000 + "ms)");
        return;
      } else {
        if (debug.is(DebugMode.DETAILED)) debug.print("Провальная проверка " + set + " на возможность выполнения");
      }
    }
  }
  
  protected List<Command> getSubcommandsFor(CommandSender sender) {
    
    if (debug.is(DebugMode.DETAILED)) debug.print("Получение списка подкоманд для " + sender.getName() + "");
    
    List<Command> list = new ArrayList<>();
    for (Command command : subcommands) {
      if (command.canPerformedBy(sender)) {
        list.add(command);
      }
    }
    return list;
  }
  
  protected Command getSubcommandFor(String arg, CommandSender sender) {
    
    if (debug.is(DebugMode.DETAILED))
      debug.print("Получение списка подкоманд для " + sender.getName() + " по введённому [" + arg + "]");
    
    for (Command command : subcommands) {
      if ((command.name.equalsIgnoreCase(arg) || command.aliases.contains(arg)) && command.canPerformedBy(sender)) {
        return command;
      }
    }
    return null;
  }
  
  protected List<ArgumentSet> getArgumentSetsFor(CommandSender sender) {
    
    if (debug.is(DebugMode.DETAILED)) debug.print("Получение списка аргументсетов для " + sender.getName());
    
    List<ArgumentSet> list = new ArrayList<>();
    for (ArgumentSet arg : argumentSets) {
      if (arg.canPerformedBy(sender)) {
        list.add(arg);
      }
    }
    return list;
  }
  
  protected boolean hasArgumentSet(CommandSender sender, String... args) {
    
    if (debug.is(DebugMode.DETAILED))
      debug.print("Поиск аргументсета в " + this.name + " с " + Arrays.toString(args) + " для " + sender.getName());
    
    for (ArgumentSet set : argumentSets) {
      if (set.isArgumentsFit(args) && set.canPerformedBy(sender)) {
        
        if (debug.is(DebugMode.DETAILED)) debug.print("Найдено");
        return true;
      }
    }
    
    if (debug.is(DebugMode.DETAILED)) debug.print("Нет совпадений");
    
    return false;
  }
  
  protected void helpFor(CommandSender sender, String[] args) {
    if (help == null) {
      
      if (debug.is(DebugMode.DETAILED)) debug.print("Генерация автохелпа для " + sender.getName() + " в " + this.name);
      sendAutoHelp(sender);
      return;
    }
    
    if (debug.is(DebugMode.DETAILED))
      debug.print("Вывод кастомного хелпа для " + sender.getName() + " в " + this.name + "");
    help.sendTo(sender, args);
  }
  
  private void sendAutoHelp(CommandSender sender) {
    Component written = Component.text(getFullCommandPath()).color(writtenColor);
    
    List<Component> toSend = new ArrayList<>();
    
    for (Command subcommand : getSubcommandsFor(sender)) {
      toSend.add(written
                    .append(Component.text(" " + subcommand.name + " ", subcommandColor))
                    .append(sender.isOp() ? Component.text(" " + subcommand.permission, permissionColor) : Component.empty())
      );
    }
    
    for (ArgumentSet argumentSet : getArgumentSetsFor(sender)) {
      for (Component component : argumentSet.getHelp(sender)) {
        toSend.add(written.append(component));
      }
      
      if (argumentSet.hasHelp()) {
        toSend.add(Component.text("↳ ").append(argumentSet.help).color(comment));
        toSend.add(Component.empty());
      }
    }
    
    sendDescription(sender);
    
    sender.sendMessage("");
    
    if (toSend.isEmpty()) {
      MessagesUtils.send(sender, Component.text("Для вас нет доступных продолжений этой команды", text));
    } else {
      MessagesUtils.send(sender, Component.text("Возможные продолжения команды:", text));
      sender.sendMessage("");
      
      for (Component row : toSend) {
        MessagesUtils.send(sender, row);
      }
    }
  }
  
  private void sendDescription(CommandSender sender) {
    if (description != null) {
      sender.sendMessage("");
      for (String part : description.split("\n")) {
        MessagesUtils.send(sender, Component.text(part, text));
      }
      sender.sendMessage("");
    }
  }
  
  @NotNull
  private String getFullCommandPath() {
    StringBuilder writtenString = new StringBuilder();
    writtenString.insert(0, name);
    
    Command rootCommand = this;
    
    while (rootCommand.isSubcommand) {
      rootCommand = rootCommand.parent;
      writtenString.insert(0, rootCommand.name + " ");
    }
    
    writtenString.insert(0, "  /");
    return writtenString.toString();
  }
  
  Command getRootCommand() {
    if (isSubcommand) return parent.getRootCommand();
    return this;
  }
  
  PermissionGenerationStrategy getStrategy() {
    return getRootCommand().strategy;
  }
}
