package ru.cwcode.commands.preconditions;

import ru.cwcode.commands.Command;

public class CommandPreconditionResult {
  Command command;
  PreconditionResult result;
  
  public CommandPreconditionResult(Command command, PreconditionResult result) {
    this.command = command;
    this.result = result;
  }
  
  public Command getCommand() {
    return command;
  }
  
  public PreconditionResult getResult() {
    return result;
  }
}
