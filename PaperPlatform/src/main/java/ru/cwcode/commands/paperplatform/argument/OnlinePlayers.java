package ru.cwcode.commands.paperplatform.argument;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayers extends Argument {
   public OnlinePlayers() {
   }

   @Override
   public boolean valid(String raw) {
      return Bukkit.getPlayer(raw) != null;
   }

   @Override
   public List<String> completions(Sender sender) {
      List<String> list = new ArrayList<>();
      for (Player player : Bukkit.getOnlinePlayers()) {
         list.add(player.getName());
      }
      return list;
   }

   @Override
   public String argumentName() {
      return "игрок в сети";
   }

   @Override
   public Component invalidMessage(Command command, Sender sender, String written) {
      return Component.text("игрок " + written + " не онлайн", command.getColorScheme().main());
   }
}
