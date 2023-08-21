package ru.cwcode.commands.paperplatform;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.paper.PaperExecutor;

public abstract class AsyncExecutor extends PaperExecutor {
  @Override
  public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
    this.parser = new ArgumentParser(args, argumentSet);
    this.sender = sender;
    this.command = command;
    
    Bukkit.getScheduler().runTaskAsynchronously(PaperMain.plugin, () -> {
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
