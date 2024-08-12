package ru.cwcode.commands.api;

import ru.cwcode.commands.Command;
import ru.cwcode.cwutils.logger.Logger;

public abstract class Platform {
  public abstract void registerCommand(Command command);
  
  public abstract Logger getLogger();
  
  public abstract void handleExecutionException(Exception exception, Command command, Sender sender);
}
