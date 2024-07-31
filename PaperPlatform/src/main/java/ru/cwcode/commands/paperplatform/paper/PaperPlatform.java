package ru.cwcode.commands.paperplatform.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Logger;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;
import ru.cwcode.cwutils.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PaperPlatform extends Platform {
  private final Logger logger = new PaperLogger();
  
  HashMap<String, PaperCommand> registeredCommands = new HashMap<>();
  
  @Override
  public void registerCommand(Command command) {
    
    PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getName());
    
    if (pluginCommand == null) {
      logger.warn("Команда `" + command.getName() + "` не может быть зарегистрирована по причине отсутствия в plugin.yml");
      return;
    }
    
    PaperCommand paperCommand = new PaperCommand(command);
    pluginCommand.setExecutor(paperCommand.getCommandParser());
    
    CommandMap commandMap = ReflectionUtils.getFieldValue(Bukkit.getPluginManager(), "commandMap", CommandMap.class);
    
    Plugin plugin = pluginCommand.getPlugin();
    
    if (!command.aliases().isEmpty()) {
      command.aliases(new ArrayList<>(CollectionUtils.combine(pluginCommand.getAliases(), command.aliases())));
      registerNewAliases(command, pluginCommand, commandMap, plugin);
    }
    
    if (pluginCommand.getDescription().isEmpty()) pluginCommand.setDescription(command.description());
    
    if (pluginCommand.getPermission() == null) pluginCommand.setPermission(command.getPermission());
    
    
    String prefix = pluginCommand.getPlugin().getName().toLowerCase();
    
    registeredCommands.put(command.getName(), paperCommand);
    registeredCommands.put(prefix + ":" + command.getName(), paperCommand);
    
    for (String alias : command.aliases()) {
      registeredCommands.put(alias, paperCommand);
      registeredCommands.put(prefix + ":" + alias, paperCommand);
    }
    
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
  
  @Override
  public void handleExecutionException(Exception exception, Command command, Sender sender) {
    if (exception instanceof MessageReturn) {
      MessageReturn messageReturn = (MessageReturn) exception;
      if (messageReturn.isStyled()) {
        sender.sendMessage(messageReturn.getComponentMessage());
      } else {
        sender.sendMessage(messageReturn.getComponentMessage()
                                        .color(command.getColorScheme().main()));
      }
      return;
    } else if (exception instanceof TargetableMessageReturn) {
      sender.sendMessage(((TargetableMessageReturn) exception).getMessage(((PaperSender) sender).getCommandSender()));
      return;
    }
    
    String localizedMessage = exception.getLocalizedMessage();
    if (localizedMessage == null) localizedMessage = "Неизвестная ошибка";
    
    sender.sendMessage(Component.text(localizedMessage, command.getColorScheme().main()));
    
    CommandsAPI.getPlatform().getLogger().warn("Ошибка при исполнении " + this.getClass().getName());
    exception.printStackTrace();
  }
  
  public Optional<PaperCommand> getRegisteredCommand(String name) {
    return Optional.ofNullable(registeredCommands.get(name));
  }
}
