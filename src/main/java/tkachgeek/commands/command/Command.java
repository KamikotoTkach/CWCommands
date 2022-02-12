package tkachgeek.commands.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import tkachgeek.commands.command.arguments.ExactStringArg;
import tkachgeek.commands.command.arguments.executor.Executor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Command {
  static TextColor text = TextColor.fromHexString("#00a6f0");
  static TextColor argument = TextColor.fromHexString("#00baff");
  static TextColor argumentOptional = TextColor.fromHexString("#02d7ff");
  static TextColor subcommandColor = TextColor.fromHexString("#0098dc");
  static TextColor writtenColor = TextColor.fromHexString("#007ab5");
  static TextColor permissionColor = TextColor.fromHexString("#055080");
  
  final String name;
  final List<ArgumentSet> argumentSets = new ArrayList<>();
  boolean isSubcommand = false;
  String description;
  String permission;
  Help help;
  Command parent = null;
  Command[] subcommands = new Command[]{};
  
  public Command(String name) {
    this(name, null);
  }
  
  public Command(String name, String permission) {
    this.name = name;
    this.permission = permission;
  }
  
  public Command(String name, String permission, Executor executor) {
    this(name, permission);
    arguments(new ArgumentSet(executor, permission));
  }
  
  public Command subCommands(Command... subcommands) {
    this.subcommands = subcommands;
    for (Command subcommand : subcommands) {
      subcommand.isSubcommand = true;
      subcommand.parent = this;
    }
    
    return this;
  }
  
  protected void updatePermissions(String permissions) {
    
    if (isSubcommand) {
      if (permission == null) {
        permission = permissions + "." + name;
      } else {
        if (!permission.isEmpty()) {
          permission = permissions + "." + permission;
        }
      }
    } else if (permission == null) {
      permission = name;
    }
    
    for (Command subcommand : subcommands) {
      subcommand.updatePermissions(permission);
    }
    
    if (permission != null && !permission.isEmpty()) {
      for (ArgumentSet argumentSet : argumentSets) {
        if (argumentSet.permission != null && !argumentSet.permission.isEmpty()) {
          argumentSet.permission = permissions + "." + argumentSet.permission;
        } else {
          argumentSet.permission = "";
        }
      }
    }
  }
  
  public void register(JavaPlugin plugin) {
    if (isSubcommand) return;
    
    updatePermissions(permission);
    
    try {
      plugin.getCommand(name).setTabCompleter(new TabCompleter(this));
      plugin.getCommand(name).setExecutor(new CommandParser(this));
    } catch (Exception e) {
      Bukkit.getLogger().warning("Не удалось зарегистрировать команду " + name + " в виду её отсутствия в plugin.yml");
    }
  }
  
  public Command arguments(ArgumentSet... arguments) {
    this.argumentSets.addAll(List.of(arguments));
    for (ArgumentSet set : arguments) {
      if (set.optionalStart > 0) {
        for (int i = set.arguments.length - 1; i >= set.optionalStart; i--) {
          argumentSets.add(new ArgumentSet(set.executor, set.permission, Arrays.copyOfRange(set.arguments, 0, i)));
        }
      }
    }
    return this;
  }
  
  public Command help(Help help) {
    this.help = help;
    return this;
  }
  
  public Command description(String shortDescription) {
    this.description = shortDescription;
    return this;
  }
  
  protected void onError(CommandSender sender, String[] args) {
    helpFor(sender, args);
  }
  
  protected boolean canPerformedBy(CommandSender sender) {
    return permission != null && (sender.hasPermission(permission) || permission.isEmpty()) || sender.isOp();
  }
  
  protected void onExecute(CommandSender sender, String[] args) {
    for (ArgumentSet set : argumentSets) {
      if (set.isArgumentsFit(args) && set.canPerformedBy(sender)) {
        set.executor.prepare(sender, args, set);
        return;
      }
    }
  }
  
  protected List<Command> getSubcommandsFor(CommandSender sender) {
    return Arrays.stream(subcommands)
       .filter(command -> command.canPerformedBy(sender)).collect(Collectors.toList());
  }
  
  protected Command getSubcommandFor(String arg, CommandSender sender) {
    return Arrays.stream(subcommands)
       .filter(command -> command.name.equalsIgnoreCase(arg) && command.canPerformedBy(sender))
       .findFirst().orElse(null);
  }
  
  protected List<ArgumentSet> getArgumentSetsFor(CommandSender sender) {
    return argumentSets.stream().filter(arg -> arg.canPerformedBy(sender)).collect(Collectors.toList());
  }
  
  protected boolean hasArgumentSet(CommandSender sender, String... args) {
    for (ArgumentSet set : argumentSets) {
      if (set.isArgumentsFit(args) && set.canPerformedBy(sender)) return true;
    }
    return false;
  }
  
  protected void helpFor(CommandSender sender, String[] args) {
    if (help == null) {
      sendAutoHelp(sender);
      return;
    }
    help.sendTo(sender, args);
  }
  
  private void sendAutoHelp(CommandSender sender) {
    StringBuilder writtenString = new StringBuilder();
    writtenString.insert(0, name);
    
    Command rootCommand = this;
    
    while (rootCommand.isSubcommand) {
      rootCommand = rootCommand.parent;
      writtenString.insert(0, rootCommand.name + " ");
    }
    
    writtenString.insert(0, "  /");
    
    Component written = Component.text(writtenString.toString()).color(writtenColor);
    
    List<AbstractMap.SimpleEntry<Component, Component>> toSend = new ArrayList<>();
    List<Command> subcommands = getSubcommandsFor(sender);
    
    for (Command subcommand : subcommands) {
      toSend.add(new AbstractMap.SimpleEntry<>(
         written.append(Component.text(" " + subcommand.name + " ", subcommandColor)),
         sender.isOp() ? Component.text(" " + subcommand.permission, permissionColor) : Component.empty()));
    }
    
    for (ArgumentSet argumentSet : getArgumentSetsFor(sender)) {
      toSend.add(new AbstractMap.SimpleEntry<>(
         written.append(Arrays.stream(argumentSet.arguments).map(x -> {
              if (x.isOptional()) {
                return Component.text("[" + x.argumentName() + "]", argumentOptional);
              } else if (x instanceof ExactStringArg) {
                return Component.text(x.argumentName(), subcommandColor);
              } else {
                return Component.text("<" + x.argumentName() + ">", argument);
              }
            }).reduce(Component.empty(), (a, x) -> a.append(Component.space())
               .append(x))
            .append(Component.text(argumentSet.spacedLastArgument ? "..." : ""))),
         sender.isOp() ? Component.text(" " + argumentSet.permission, permissionColor) : Component.empty()));
    }
    
    if (description != null) {
      sender.sendMessage("");
      for (String part : description.split("\n")) {
        sender.sendMessage(Component.text(part, text));
      }
      sender.sendMessage("");
    }
    
    if (toSend.isEmpty()) {
      sender.sendMessage(Component.text("Для вас нет доступных продолжений этой команды", text));
    } else {
      sender.sendMessage(Component.text("Возможные продолжения команды:", text));
      sender.sendMessage("");
      
      for (AbstractMap.SimpleEntry<Component, Component> component : toSend) {
        sender.sendMessage(component.getKey().clickEvent(
           ClickEvent.suggestCommand(PlainTextComponentSerializer.plainText().serialize(component.getKey()).strip())).append(component.getValue())
        );
      }
    }
  }
}
