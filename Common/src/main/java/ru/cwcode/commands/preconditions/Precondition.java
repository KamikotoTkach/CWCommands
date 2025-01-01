package ru.cwcode.commands.preconditions;

import ru.cwcode.commands.api.Sender;

public interface Precondition {
  boolean canExecute(Sender sender);
  
  default boolean canSee(Sender sender) {
    return true;
  }
  
  String cannotExecuteFeedback(Sender sender);
}
