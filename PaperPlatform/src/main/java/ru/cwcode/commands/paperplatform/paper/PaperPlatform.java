package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Logger;
import ru.cwcode.commands.api.Platform;
import tkachgeek.tkachutils.collections.CollectionUtils;
import tkachgeek.tkachutils.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PaperPlatform extends Platform {
  private final Logger logger = new PaperLogger();
  
  @Override
  public void registerCommand(Command command) {
    
    PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getName());
    
    if (pluginCommand == null) {
      logger.warn("Команда `" + command.getName() + "` не может быть зарегистрирована по причине отсутствия в plugin.yml");
      return;
    }
    
    PaperCommand paperCommand = new PaperCommand(command);
    pluginCommand.setExecutor(paperCommand.getCommandParser());
    pluginCommand.setTabCompleter(paperCommand.getTabCompleter());
    
    CommandMap commandMap = ReflectionUtils.getFieldValue(Bukkit.getPluginManager(), "commandMap", CommandMap.class);
    
    Plugin plugin = pluginCommand.getPlugin();
    
    if (!command.aliases().isEmpty()) {
      command.aliases(new ArrayList<>(CollectionUtils.combine(pluginCommand.getAliases(), command.aliases())));
      registerNewAliases(command, pluginCommand, commandMap, plugin);
    }
    
    if (pluginCommand.getDescription().isEmpty()) pluginCommand.setDescription(command.description());
    
    if (pluginCommand.getPermission() == null) pluginCommand.setPermission(command.permission());
  }
  
  private void registerNewAliases(Command command, org.bukkit.command.Command paperCommand, CommandMap commandMap, Plugin plugin) {
    
    try {
      Field activeAliases = paperCommand.getClass().getSuperclass().getDeclaredField("activeAliases");
      activeAliases.setAccessible(true);
      ((List<String>) activeAliases.get(paperCommand)).addAll(command.aliases());
      
      Field aliasesField = paperCommand.getClass().getSuperclass().getDeclaredField("aliases");
      aliasesField.setAccessible(true);
      ((List<String>) aliasesField.get(paperCommand)).addAll(command.aliases());
      
      Method registerAlias = commandMap.getClass().getMethod("register", String.class, org.bukkit.command.Command.class);
      registerAlias.setAccessible(true);
      
      registerAlias.invoke(commandMap, plugin.getName(), paperCommand);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
      logger.warn("Не удалось зарегистрировать алиасы для `" + command.getName() + "`");
    }
  }
  
  @Override
  public Logger getLogger() {
    return this.logger;
  }
}
