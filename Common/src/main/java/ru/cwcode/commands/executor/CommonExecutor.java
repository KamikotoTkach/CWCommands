package ru.cwcode.commands.executor;

import net.kyori.adventure.audience.Audience;

import java.util.Optional;

public abstract class CommonExecutor extends AbstractExecutor {
  
  @Override
  public void handleError(Exception exception) {
    exception.printStackTrace();
  }
  
  @Override
  protected Audience sender() {
    return sender.getAudience();
  }
  
  @Override
  protected Optional<? extends Audience> argP(int index) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
