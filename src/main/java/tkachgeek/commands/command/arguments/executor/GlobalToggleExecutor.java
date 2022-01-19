package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;

public abstract class GlobalToggleExecutor extends Executor {
  boolean state;
  
  public GlobalToggleExecutor(boolean initialState) {
    this.state = initialState;
  }
  
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet) {
    super.prepare(sender, args, argumentSet);
    if (state) {
      onEnable();
    } else {
      onDisable();
    }
    state = !state;
  }
  
  public abstract void onEnable();
  
  public abstract void onDisable();
}
