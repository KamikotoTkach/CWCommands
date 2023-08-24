package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Logger;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.velocityplatform.VelocityMain;

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
   public void registerCommand(Command command) {
      try {
         CommandManager commandManager = this.server.getCommandManager();
         commandManager.register(
               commandManager.metaBuilder(command.getName()).plugin(this.plugin).build(),
               new VelocityCommand(command)
         );
      } catch (Exception e) {
         this.getLogger().warn(String.format("Не удалось зарегистрировать команду %s в виду её отсутствия в plugin.yml", command.getName()));
      }
   }
}
