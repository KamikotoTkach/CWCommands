package ru.cwcode.commands.executor;

import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;

import java.util.Date;
import java.util.WeakHashMap;

public abstract class AbstractLocalTimedExecutor extends AbstractExecutor {
  WeakHashMap<Sender, Long> lastExecutionTime = new WeakHashMap<>();
  long delay;
  
  public AbstractLocalTimedExecutor(long delayMillis) {
    this.delay = delayMillis;
  }
  
  @Override
  public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
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
