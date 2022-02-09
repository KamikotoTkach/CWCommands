package tkachgeek.commands.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Arrays;

public class CommandParser implements CommandExecutor {
  final Command node;
  
  public CommandParser(Command command) {
    node = command;
  }
  
  public static AbstractMap.SimpleEntry<Command, Integer> parse(Command parentCommand, CommandSender sender, String... args) {
    int deep = 0;
    
    while (args.length > 0) {
      Command foundedCommand = parentCommand.getSubcommandFor(args[0], sender);
      if (foundedCommand != null) {
        deep++;
        parentCommand = foundedCommand;
        args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{};
      } else {
        break;
      }
    }
    return new AbstractMap.SimpleEntry<>(parentCommand, deep);
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
    AbstractMap.SimpleEntry<Command, Integer> parseResult = parse(node, sender, args);
    
    Command cmd = parseResult.getKey();
    int deep = parseResult.getValue();
    
    args = args.length > deep ? Arrays.copyOfRange(args, deep, args.length) : new String[]{};
    
    if (cmd.hasArgumentSet(sender, args)) {
      cmd.onExecute(sender, args);
    } else {
      cmd.onError(sender, args);
    }
    return true;
  }
}
