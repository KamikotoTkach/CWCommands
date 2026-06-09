package ru.cwcode.commands.paperplatform.paper;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.PaperMain;
import ru.cwcode.cwutils.confirmable.ConfirmAPI;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class PaperSender implements Sender {
  Reference<CommandSender> sender;
  
  public CommandSender getCommandSender() {
    return sender.get();
  }
  
  public PaperSender(CommandSender sender) {
    this.sender = new WeakReference<>(sender);
  }
  
  @Override
  public boolean hasPermission(String str) {
    return sender.get().hasPermission(str);
  }
  
  @Override
  public String getName() {
    return sender.get().getName();
  }
  
  @Override
  public void sendMessage(Component line) {
    sender.get().sendMessage(line);
  }
  
  @Override
  public boolean isPlayer() {
    return sender.get() instanceof Player;
  }
  
  @Override
  public void sendMessage(TargetableMessageReturn targetable) {
    sender.get().sendMessage(targetable.getMessage(sender.get()));
  }
  
  @Override
  public void sendMessage(String message) {
    sender.get().sendMessage(Component.text(message));
  }
  
  @Override
  public void confirm(String confirmableString, long timeToConfirm, Runnable onConfirm, Runnable onExpired) {
    ConfirmAPI.requestBuilder(sender.get(), confirmableString, timeToConfirm)
              .success(onConfirm)
              .expired(onExpired)
              .register(PaperMain.plugin);
  }
  
  @Override
  public Audience getAudience() {
    return sender.get();
  }
  
  public Player getPlayer() {
    return (Player) sender.get();
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    PaperSender that = (PaperSender) o;
    
    return Objects.equals(sender.get(), that.sender.get());
  }
  
  @Override
  public int hashCode() {
    return sender.get() != null ? sender.get().hashCode() : 0;
  }
}
