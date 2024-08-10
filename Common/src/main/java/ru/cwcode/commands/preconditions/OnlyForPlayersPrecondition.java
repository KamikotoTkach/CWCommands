package ru.cwcode.commands.preconditions;

import ru.cwcode.commands.api.Sender;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class OnlyForPlayersPrecondition extends Precondition {
  static OnlyForPlayersPrecondition instance;
  
  private OnlyForPlayersPrecondition() {
  }
  
  public static OnlyForPlayersPrecondition getInstance() {
    if (instance == null) instance = new OnlyForPlayersPrecondition();
    return instance;
  }
  
  @Override
  public boolean canExecute(Sender sender) {
    return sender.isPlayer();
  }
  
  @Override
  public boolean canSee(Sender sender) {
    return canExecute(sender);
  }
  
  @Override
  public String cannotExecuteFeedback(Sender sender) {
    return l10n.get("precondition.onlyForPlayer.cannotExecute");
  }
}
