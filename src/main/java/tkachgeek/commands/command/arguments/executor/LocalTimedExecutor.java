package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;

import java.util.Date;
import java.util.HashMap;

public abstract class LocalTimedExecutor extends Executor {
  HashMap<CommandSender, Long> lastExecutionTime = new HashMap<>();
  Long delay;
  
  public LocalTimedExecutor(Date delay) {
    this.delay = delay.getTime();
  }
  
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet) {
    if (lastExecutionTime.containsKey(sender)) {
      if (System.currentTimeMillis() - lastExecutionTime.get(sender) > delay) {
        lastExecutionTime.put(sender, System.currentTimeMillis());
        commandDelayPassed();
      } else {
        commandDelayNotPassed();
      }
    }
    super.prepare(sender, args, argumentSet);
  }
  
  protected abstract void commandDelayNotPassed();
  
  protected abstract void commandDelayPassed();
}
