package ru.cwcode.commands.test;

import ru.cwcode.cwutils.logger.Logger;

class SoutLogger implements Logger {
  @Override
  public void info(String message) {
    System.out.println(message);
  }
  
  @Override
  public void warn(String message) {
    System.out.println(message);
  }
  
  @Override
  public void error(String message) {
    System.out.println(message);
  }
}
