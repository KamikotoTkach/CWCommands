package ru.cwcode.commands.paperplatform.paper;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.PaperMain;
import tkachgeek.tkachutils.confirmable.ConfirmAPI;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

import java.util.Objects;

public class PaperSender implements Sender {
  CommandSender sender;
  
  public CommandSender getCommandSender() {
    return sender;
  }
  
  public PaperSender(CommandSender sender) {
    this.sender = sender;
  }
  
  @Override
  public boolean hasPermission(String str) {
    return sender.hasPermission(str);
  }
  
  @Override
  public String getName() {
    return sender.getName();
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
    sender.sendMessage(targetable.getMessage(sender));
  }
  
  @Override
  public void sendMessage(String message) {
    sender.sendMessage(Component.text(message));
  }
  
  @Override
  public void confirm(String confirmableString, long timeToConfirm, Runnable onConfirm, Runnable onExpired) {
    ConfirmAPI.requestBuilder(sender, confirmableString, timeToConfirm)
              .success(onConfirm)
              .expired(onExpired)
              .register(PaperMain.plugin);
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
    
    PaperSender that = (PaperSender) o;
    
    return Objects.equals(sender, that.sender);
  }
  
  @Override
  public int hashCode() {
    return sender != null ? sender.hashCode() : 0;
  }
}
