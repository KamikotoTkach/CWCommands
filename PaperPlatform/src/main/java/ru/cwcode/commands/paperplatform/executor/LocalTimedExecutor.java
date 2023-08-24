package ru.cwcode.commands.paperplatform.executor;

import ru.cwcode.commands.executor.AbstractLocalTimedExecutor;

public abstract class LocalTimedExecutor extends AbstractLocalTimedExecutor {
  
  public LocalTimedExecutor(long delay) {
    super(delay);
  }
}
