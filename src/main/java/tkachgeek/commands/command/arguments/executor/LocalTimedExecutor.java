package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;

import java.util.Date;
import java.util.HashMap;
import java.util.WeakHashMap;

public abstract class LocalTimedExecutor extends Executor {
  WeakHashMap<CommandSender, Long> lastExecutionTime = new WeakHashMap<>();
  long delay;
  
  public LocalTimedExecutor(Date delay) {
    this.delay = delay.getTime();
  }
  
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet, Command command) {
    if (lastExecutionTime.containsKey(sender)) {
      if (System.currentTimeMillis() - lastExecutionTime.get(sender) > delay) {
        lastExecutionTime.put(sender, System.currentTimeMillis());
        commandDelayPassed();
      } else {
        commandDelayNotPassed();
      }
    }
    super.prepare(sender, args, argumentSet, command);
  }
  
  protected abstract void commandDelayNotPassed();
  
  protected abstract void commandDelayPassed();
}
