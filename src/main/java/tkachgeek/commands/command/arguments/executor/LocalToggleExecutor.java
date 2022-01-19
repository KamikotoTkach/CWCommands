package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;

import java.util.HashMap;

public abstract class LocalToggleExecutor extends Executor {
  HashMap<CommandSender, Boolean> state = new HashMap<>();
  boolean initialState;
  
  public LocalToggleExecutor(boolean initialState) {
    this.initialState = initialState;
  }
  
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet) {
    super.prepare(sender, args, argumentSet);
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
