package ru.cwcode.commands.preconditions.impl;

import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.preconditions.Precondition;

public class HiddenCommandPrecondition implements Precondition {
  @Override
  public boolean canSee(Sender sender) {
    return false;
  }
  
  @Override
  public boolean canExecute(Sender sender) {
    return true;
  }
  
  @Override
  public String cannotExecuteFeedback(Sender sender) {
    return "";
  }
}
