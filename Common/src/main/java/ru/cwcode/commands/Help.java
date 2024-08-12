package ru.cwcode.commands;

import ru.cwcode.commands.api.Sender;

public interface Help {
  void sendTo(Sender sender, String[] args);
  
  default void sendTo(Sender sender, Command command, String label, String[] args) {
    sendTo(sender, args);
  }
}
