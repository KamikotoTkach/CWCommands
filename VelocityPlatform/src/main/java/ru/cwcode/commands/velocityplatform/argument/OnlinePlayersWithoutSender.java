package ru.cwcode.commands.velocityplatform.argument;

import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.velocity.VelocitySender;

import java.util.List;

public class OnlinePlayersWithoutSender extends OnlinePlayers {
   @Override
   public List<String> completions(Sender sender) {
      List<String> completions = super.completions(sender);

      if (sender.isPlayer() && sender instanceof VelocitySender velocitySender) {
         completions.remove(velocitySender.getPlayer().getUsername());
      }

      return completions;
   }

   @Override
   public String argumentName() {
      return super.argumentName();
   }
}
