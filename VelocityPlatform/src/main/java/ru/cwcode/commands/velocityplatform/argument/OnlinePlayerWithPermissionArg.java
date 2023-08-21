package ru.cwcode.commands.velocityplatform.argument;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.commands.velocityplatform.velocity.VelocitySender;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayerWithPermissionArg extends Argument {
   String permission;
   String argumentName = null;

   public OnlinePlayerWithPermissionArg(String permission) {
      this.permission = permission;
   }

   public OnlinePlayerWithPermissionArg(String permission, String argumentName) {
      this.permission = permission;
      this.argumentName = permission;
   }

   @Override
   public boolean valid(String raw) {
      Player player = VelocityMain.getPlatform().getServer().getPlayer(raw).orElse(null);
      return player != null && player.hasPermission(permission);
   }

   @Override
   public List<String> completions(Sender sender) {
      List<String> list = new ArrayList<>();
      if (sender.isPlayer() && sender instanceof VelocitySender velocitySender) {
         ServerConnection server = velocitySender.getPlayer().getCurrentServer().orElse(null);
         if (server != null) {
            for (Player player : server.getServer().getPlayersConnected()) {
               if (player.hasPermission(permission)) {
                  list.add(player.getUsername());
               }
            }
         }
      }
      return list;
   }

   @Override
   public String argumentName() {
      return argumentName == null ? "Игрок с " + permission : argumentName;
   }
}
