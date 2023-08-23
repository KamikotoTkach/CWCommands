package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import tkachgeek.tkachutils.confirmable.velocity.ConfirmAPI;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

public class VelocitySender implements Sender {
   CommandSource sender;

   public VelocitySender(CommandSource sender) {
      this.sender = sender;
   }

   @Override
   public boolean hasPermission(String str) {
      return sender.hasPermission(str);
   }

   @Override
   public void sendMessage(Component line) {
      sender.sendMessage(line);
   }

   @Override
   public boolean isPlayer() {
      return sender instanceof Player;
   }

   @Override
   public void sendMessage(TargetableMessageReturn targetable) {
      throw new RuntimeException("TargetableMessageReturn not supported on Velocity");
   }

   @Override
   public void sendMessage(String message) {
      sender.sendMessage(Component.text(message));
   }

   @Override
   public void confirm(String confirmableString, long timeToConfirm, Runnable onConfirm, Runnable onExpired) {
      if (!this.isPlayer()) return;
      ConfirmAPI.requestBuilder(this.getPlayer(), confirmableString, timeToConfirm)
                .success(onConfirm)
                .expired(onExpired)
                .register(VelocityMain.getPlatform().getServer(), VelocityMain.getPlatform().getPlugin());
   }

   public Player getPlayer() {
      return (Player) sender;
   }
}
