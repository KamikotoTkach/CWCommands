package ru.cwcode.commands.test;

import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.cwutils.logger.Logger;

public class TestConfigPlatform extends Platform {
  Logger logger = new SoutLogger();
  
  @Override
  public void registerCommand(Command command) {
  
  }
  
  @Override
  public Logger getLogger() {
    return logger;
  }
  
  @Override
  public void handleExecutionException(Exception exception, Command command, Sender sender) {
    exception.printStackTrace();
  }
}
