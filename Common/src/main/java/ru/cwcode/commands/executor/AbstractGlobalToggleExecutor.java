package ru.cwcode.commands.executor;

import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

public abstract class AbstractGlobalToggleExecutor extends AbstractExecutor {
  boolean state;
  
  public AbstractGlobalToggleExecutor(boolean initialState) {
    this.state = initialState;
  }
  
  @Override
  public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
    super.prepare(sender, args, argumentSet, command);
    if (state) {
      onEnable();
    } else {
      onDisable();
    }
    state = !state;
  }
  
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
  
  }
  
  
  public abstract void onEnable();
  
  public abstract void onDisable();
}
