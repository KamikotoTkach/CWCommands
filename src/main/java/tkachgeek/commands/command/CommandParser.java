package tkachgeek.commands.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandParser implements CommandExecutor {
  final Command node;
  
  public CommandParser(Command command) {
    node = command;
  }
  
  public static Result parse(Command parentCommand, CommandSender sender, String... args) {
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
    return new Result(parentCommand, deep);
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
    Result parseResult = parse(node, sender, args);
    
    Command foundedCommand = parseResult.getCommand();
    int deep = parseResult.getDeep();
    
    args = args.length > deep ? Arrays.copyOfRange(args, deep, args.length) : new String[]{};
    
    ArgumentSearchResult argumentSearchResult = foundedCommand.searchForArgumentSet(sender, args);
    
    if (argumentSearchResult.isFounded()) {
      foundedCommand.onExecute(sender, args, argumentSearchResult.getFounded());
    } else {
      foundedCommand.onError(sender, args, argumentSearchResult);
    }
    
    return true;
  }
  
  static class Result {
    Command command;
    int deep;
    
    public Result(Command command, int deep) {
      this.command = command;
      this.deep = deep;
    }
    
    public Command getCommand() {
      return command;
    }
    
    public int getDeep() {
      return deep;
    }
  }
}
