package ru.cwcode.commands.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

public interface Sender {
  boolean hasPermission(String str);
  
  String getName();
  
  void sendMessage(Component line);
  
  boolean isPlayer();
  
  void sendMessage(TargetableMessageReturn targetable);
  
  void sendMessage(String message);
  
  default boolean isOp() {
    return hasPermission("*");
  }

  Audience getAudience();
  
  void confirm(String confirmableString, long timeToConfirm, Runnable onConfirm, Runnable onExpired);
}
