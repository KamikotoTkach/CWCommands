package tkachgeek.commands.command.arguments.bukkit;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.Command;

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
   public List<String> completions(CommandSender sender) {
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
   public Component invalidMessage(Command command, CommandSender sender, String written) {
      return Component.text("игрок " + written + " не онлайн", command.getColorScheme().main());
   }
}
