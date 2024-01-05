package ru.cwcode.commands.preconditions;

import ru.cwcode.commands.api.Sender;

public abstract class Precondition {
  public abstract boolean canExecute(Sender sender);
  public boolean canSee(Sender sender) {
    return true;
  }
  public abstract String cannotExecuteFeedback(Sender sender);
}
