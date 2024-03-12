package ru.cwcode.commands;

import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.preconditions.CommandPreconditionResult;

import java.util.Arrays;

public abstract class CommandParser {
  final Command node;
  
  public CommandParser(Command command) {
    node = command;
  }
  
  public boolean onCommandExecute(@NotNull Sender sender, String label, String[] args) {
    Result parseResult = parse(node, sender, args);
    
    Command foundedCommand = parseResult.getCommand();
    int deep = parseResult.getDeep();
    
    args = args.length > deep ? Arrays.copyOfRange(args, deep, args.length) : new String[]{};
    
    ArgumentSearchResult argumentSearchResult = foundedCommand.searchForArgumentSet(sender, args);
    
    if (argumentSearchResult.isFounded() && argumentSearchResult.getErrorMessage() == null) {
      foundedCommand.onExecute(sender, args, argumentSearchResult.getFounded());
    } else {
      foundedCommand.onError(sender, label, args, argumentSearchResult);
    }
    
    return true;
  }
  
  public static Result parse(Command parentCommand, Sender sender, String... args) {
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
  
  public static class Result {
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
