package ru.cwcode.commands.paperplatform.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.PaperMain;
import tkachgeek.tkachutils.confirmable.ConfirmAPI;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

public class PaperSender implements Sender {
  CommandSender sender;
  
  public PaperSender(CommandSender sender) {
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
  
  public Player getPlayer() {
    return (Player) sender;
  }
}
