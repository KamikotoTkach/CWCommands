package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;

public abstract class GlobalToggleExecutor extends Executor {
  boolean state;
  
  public GlobalToggleExecutor(boolean initialState) {
    this.state = initialState;
  }
  
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet, Command command) {
    super.prepare(sender, args, argumentSet, command);
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
