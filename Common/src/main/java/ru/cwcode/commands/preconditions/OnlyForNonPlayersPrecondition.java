package ru.cwcode.commands.preconditions;

import ru.cwcode.commands.api.Sender;

public class OnlyForNonPlayersPrecondition extends Precondition {
  static OnlyForNonPlayersPrecondition instance;
  
  private OnlyForNonPlayersPrecondition() {
  }
  
  public static OnlyForNonPlayersPrecondition getInstance() {
    if (instance == null) instance = new OnlyForNonPlayersPrecondition();
    return instance;
  }
  
  @Override
  public boolean canExecute(Sender sender) {
    return !sender.isPlayer();
  }
  
  @Override
  public boolean canSee(Sender sender) {
    return canExecute(sender);
  }
  
  @Override
  public String cannotExecuteFeedback(Sender sender) {
    return "Только для не-игроков";
  }
}
