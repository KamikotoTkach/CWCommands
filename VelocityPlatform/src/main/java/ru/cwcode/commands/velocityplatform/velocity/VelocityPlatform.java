package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Logger;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import tkachgeek.tkachutils.messages.MessageReturn;

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
      if (localizedMessage == null) localizedMessage = "§Error (no message in exception)";
      
      sender.sendMessage(localizedMessage);
      
      CommandsAPI.getPlatform().getLogger().warn("Ошибка при исполнении " + this.getClass().getName());
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
         this.getLogger().warn(String.format("Не удалось зарегистрировать команду %s в виду её отсутствия в plugin.yml", command.getName()));
      }
   }
}
