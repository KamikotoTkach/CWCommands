package ru.cwcode.commands.arguments.executor;

import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.api.AbstractExecutor;

import java.util.WeakHashMap;

public abstract class LocalToggleExecutor extends AbstractExecutor {
  WeakHashMap<Sender, Boolean> state = new WeakHashMap<>();
  boolean initialState;
  
  public LocalToggleExecutor(boolean initialState) {
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
  
  public abstract void onEnable();
  
  public abstract void onDisable();
}
