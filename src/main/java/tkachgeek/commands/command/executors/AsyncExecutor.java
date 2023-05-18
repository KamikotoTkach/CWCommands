package tkachgeek.commands.command.executors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.ArgumentParser;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.executor.Executor;

public abstract class AsyncExecutor extends Executor {
  @Override
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet, Command command) {
    this.parser = new ArgumentParser(args, argumentSet);
    this.sender = sender;
    this.command = command;
    
    Bukkit.getScheduler().runTaskAsynchronously(command.getPlugin(), () -> {
      try {
        if (sender instanceof Player) {
          executeForPlayer();
        } else {
          executeForNonPlayer();
        }
      } catch (Exception e) {
        errorHandler(e);
      }
    });
  }
}
