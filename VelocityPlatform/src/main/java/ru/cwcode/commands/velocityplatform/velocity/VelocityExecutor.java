package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.proxy.Player;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.api.CommandsAPI;
import tkachgeek.tkachutils.messages.MessageReturn;

public abstract class VelocityExecutor extends AbstractExecutor {
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

   protected final Player player() {
      if (sender instanceof VelocitySender) {
         return ((VelocitySender) sender).getPlayer();
      }

      return null;
   }
}
