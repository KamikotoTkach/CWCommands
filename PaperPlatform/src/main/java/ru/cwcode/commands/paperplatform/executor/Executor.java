package ru.cwcode.commands.paperplatform.executor;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.paperplatform.paper.PaperSender;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

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
      } else if (exception instanceof TargetableMessageReturn) {
         sender.sendMessage((Component) exception);
         return;
      }

      String localizedMessage = exception.getLocalizedMessage();
      if (localizedMessage == null) localizedMessage = "Неизвестная ошибка";

      sender.sendMessage(Component.text(localizedMessage, command.getColorScheme().main()));

      CommandsAPI.getPlatform().getLogger().warn("Ошибка при исполнении " + this.getClass().getName());
      exception.printStackTrace();
   }

   @Override
   protected Audience sender() {
      return sender.getAudience();
   }

   @Override
   protected Optional<Player> argP(int index) {
      Player player = Bukkit.getPlayer(argS(index));
      if (player == null) return Optional.empty();

      return Optional.of(player);
   }

   protected final Player player() {
      if (sender instanceof PaperSender) {
         return ((PaperSender) sender).getPlayer();
      }

      return null;
   }
}
