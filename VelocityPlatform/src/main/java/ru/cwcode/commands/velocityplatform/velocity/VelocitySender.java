package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.cwutils.confirmable.velocity.ConfirmAPI;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.util.Objects;

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
  public String getName() {
    return sender instanceof Player player ? player.getUsername() : "console";
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
  
  @Override
  public Audience getAudience() {
    return sender;
  }
  
  public Player getPlayer() {
    return (Player) sender;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    VelocitySender that = (VelocitySender) o;
    
    return Objects.equals(sender, that.sender);
  }
  
  @Override
  public int hashCode() {
    return sender != null ? sender.hashCode() : 0;
  }
}
