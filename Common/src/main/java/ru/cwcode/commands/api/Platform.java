package ru.cwcode.commands.api;

import ru.cwcode.commands.Command;

import java.util.List;
import java.util.logging.Logger;

public abstract class Platform {
  public abstract void registerCommand(Command command);
  
  public Logger getLogger() {
    return Logger.getAnonymousLogger();
  }
}
