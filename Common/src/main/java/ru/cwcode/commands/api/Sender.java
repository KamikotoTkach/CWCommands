package ru.cwcode.commands.api;

import net.kyori.adventure.text.Component;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

public interface Sender {
  boolean hasPermission(String str);
  
  void sendMessage(Component line);
  
  boolean isPlayer();
  
  void sendMessage(TargetableMessageReturn targetable);
  
  void sendMessage(String message);
  
  default boolean isOp() {
    return hasPermission("*");
  }
  
  void confirm(String confirmableString, long timeToConfirm, Runnable onConfirm, Runnable onExpired);
}
