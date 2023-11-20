package ru.cwcode.commands.executor;

import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

import java.util.WeakHashMap;

public abstract class AbstractLocalToggleExecutor extends AbstractExecutor {
  WeakHashMap<Sender, Boolean> state = new WeakHashMap<>();
  boolean initialState;
  
  public AbstractLocalToggleExecutor(boolean initialState) {
    this.initialState = initialState;
  }
  
  @Override
  public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
    super.prepare(sender, args, argumentSet, command);
    boolean state = this.state.getOrDefault(sender, initialState);
    if (state) {
      onEnable();
    } else {
      onDisable();
    }
    this.state.put(sender, !state);
  }
  
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
  
  }
  
  public abstract void onEnable();
  
  public abstract void onDisable();
}
