package ru.cwcode.commands.velocityplatform.executor;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.executor.AbstractAutowiredExecutor;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.commands.velocityplatform.velocity.VelocitySender;

import java.util.Optional;

public abstract class AutowiredExecutor extends AbstractAutowiredExecutor {
   @Override
   public void handleError(Exception exception) {
      CommandsAPI.getPlatform().handleExecutionException(exception,command,sender);
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
