package ru.cwcode.commands.paperplatform.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.api.CommandsAPI;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

public abstract class PaperExecutor extends AbstractExecutor {
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
      } else if (exception instanceof TargetableMessageReturn) {
         sender.sendMessage((Component) exception);
         return;
      }

      String localizedMessage = exception.getLocalizedMessage();
      if (localizedMessage == null) localizedMessage = "§Error (no message in exception)";

      sender.sendMessage(localizedMessage);

      CommandsAPI.getPlatform().getLogger().warn("Ошибка при исполнении " + this.getClass().getName());
      exception.printStackTrace();
   }

   protected final Player player() {
      if (sender instanceof PaperSender) {
         return ((PaperSender) sender).getPlayer();
      }

      return null;
   }
}
