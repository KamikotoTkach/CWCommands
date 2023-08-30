package ru.cwcode.commands.velocityplatform.executor;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.commands.velocityplatform.velocity.VelocitySender;
import tkachgeek.tkachutils.messages.MessageReturn;

import java.util.Optional;

public abstract class Executor extends AbstractExecutor {
   @Override
   public void errorHandler(Exception exception) {
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
   protected Audience sender() {
      return sender.getAudience();
   }

   @Override
   protected Optional<Player> argP(int index) {
      Player player = VelocityMain.getPlatform().getServer().getPlayer(argS(index)).orElse(null);
      if (player == null) return Optional.empty();

      return Optional.of(player);
   }

   protected final Player player() {
      if (sender instanceof VelocitySender) {
         return ((VelocitySender) sender).getPlayer();
      }

      return null;
   }
}
