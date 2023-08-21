package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.Bukkit;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Platform;

public class PaperPlatform extends Platform {
  
  @Override
  public void registerCommand(Command command) {
    PaperCommand paperCommand = new PaperCommand(command);
    
    try {
      Bukkit.getPluginCommand(command.getName()).setExecutor(paperCommand.getCommandParser());
      Bukkit.getPluginCommand(command.getName()).setTabCompleter(paperCommand.getTabCompleter());
    } catch (Exception e) {
      getLogger().warning("Не удалось зарегистрировать команду %s в виду её отсутствия в plugin.yml".formatted(command.getName()));
    }
  }
}
