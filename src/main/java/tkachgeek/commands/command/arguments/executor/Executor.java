package tkachgeek.commands.command.arguments.executor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.ArgumentParser;
import tkachgeek.commands.command.ArgumentSet;

public abstract class Executor {
  ArgumentParser parser;
  CommandSender sender;
  
  protected final Argument arg(int index) {
    return parser.get(index);
  }
  
  protected final int argumentsAmount() {
    return parser.args.length;
  }
  
  protected final Player player() {
    return (Player) sender;
  }
  
  protected final CommandSender sender() {
    return sender;
  }
  
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet) {
    parser = new ArgumentParser(args, argumentSet);
    this.sender = sender;
    try {
      if (sender instanceof Player) {
        executeForPlayer();
      } else {
        executeForNonPlayer();
      }
    } catch (Exception exception) {
      errorHandler(exception);
    }
  }
  
  public abstract void executeForPlayer();
  
  public void executeForNonPlayer() {
    executeForPlayer();
  }
  
  
  public void errorHandler(Exception exception) {
    Bukkit.getLogger().warning("Ошибка при исполнении " + this.getClass().getName());
    exception.printStackTrace();
  }
}
