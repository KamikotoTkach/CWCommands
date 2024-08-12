package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.cwutils.logger.Logger;
import ru.cwcode.cwutils.logger.VelocityLogger;
import ru.cwcode.cwutils.messages.MessageReturn;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class VelocityPlatform extends Platform {
  private final VelocityMain plugin;
  private final ProxyServer server;
  private final Logger logger;
  
  public VelocityPlatform(VelocityMain plugin, ProxyServer server, org.slf4j.Logger logger) {
    this.plugin = plugin;
    this.server = server;
    this.logger = new VelocityLogger(logger);
  }
  
  public VelocityMain getPlugin() {
    return plugin;
  }
  
  public ProxyServer getServer() {
    return server;
  }
  
  @Override
  public Logger getLogger() {
    return logger;
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
    }
    
    String localizedMessage = exception.getLocalizedMessage();
    if (localizedMessage == null) localizedMessage = l10n.get("velocityPlatform.unknownError");
    
    sender.sendMessage(Component.text(localizedMessage, command.getColorScheme().main()));
    
    CommandsAPI.getPlatform().getLogger().warn(l10n.get("velocityPlatform.executionError", this.getClass().getName()));
    exception.printStackTrace();
  }
  
  @Override
  public void registerCommand(Command command) {
    try {
      CommandManager commandManager = this.server.getCommandManager();
      commandManager.register(
         commandManager.metaBuilder(command.getName())
                       .aliases(command.aliases().toArray(new String[0]))
                       .plugin(this.plugin)
                       .build(),
         new VelocityCommand(command)
      );
    } catch (Exception e) {
      logger.warn(l10n.get("velocityPlatform.commandIsNotRegistered", command.getName()));
    }
  }
}
