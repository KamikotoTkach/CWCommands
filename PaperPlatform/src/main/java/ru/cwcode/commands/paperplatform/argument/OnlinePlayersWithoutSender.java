package ru.cwcode.commands.paperplatform.argument;

import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.paper.PaperSender;

import java.util.List;

public class OnlinePlayersWithoutSender extends OnlinePlayers {
   @Override
   public List<String> completions(Sender sender) {
      List<String> completions = super.completions(sender);

      if (sender.isPlayer() && sender instanceof PaperSender) {
         completions.remove(((PaperSender) sender).getPlayer().getName());
      }

      return completions;
   }

   @Override
   public String argumentName() {
      return super.argumentName();
   }
}
