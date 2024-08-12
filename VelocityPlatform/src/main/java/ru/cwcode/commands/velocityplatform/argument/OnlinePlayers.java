package ru.cwcode.commands.velocityplatform.argument;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.commands.velocityplatform.velocity.VelocitySender;

import java.util.ArrayList;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class OnlinePlayers extends Argument {
   public OnlinePlayers() {
   }

   @Override
   public boolean valid(String raw) {
      return VelocityMain.getPlatform().getServer().getPlayer(raw).orElse(null) != null;
   }

   @Override
   public List<String> completions(Sender sender) {
      List<String> list = new ArrayList<>();
      if (sender.isPlayer() && sender instanceof VelocitySender velocitySender) {
         ServerConnection server = velocitySender.getPlayer().getCurrentServer().orElse(null);
         if (server != null) {
            for (Player player : server.getServer().getPlayersConnected()) {
               list.add(player.getUsername());
            }
         }
      }
      return list;
   }

   @Override
   public String argumentName() {
      return l10n.get("argument.onlinePlayer.name");
   }

   @Override
   public Component invalidMessage(Command command, Sender sender, String written) {
      return Component.text(l10n.get("argument.onlinePlayer.invalid", written), command.getColorScheme().main());
   }
   
   @Override
   public Object map() {
      return VelocityMain.getPlatform().getServer().getPlayer(raw).get();
   }
}
