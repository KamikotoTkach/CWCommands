package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;

import java.util.HashMap;
import java.util.WeakHashMap;

public abstract class LocalToggleExecutor extends Executor {
  WeakHashMap<CommandSender, Boolean> state = new WeakHashMap<>();
  boolean initialState;
  
  public LocalToggleExecutor(boolean initialState) {
    this.initialState = initialState;
  }
  
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet, Command command) {
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
