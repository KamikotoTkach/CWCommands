package ru.cwcode.commands.preconditions.processor;

import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.Permissible;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.preconditions.Precondition;

import static ru.cwcode.commands.api.CommandsAPI.l10n;
//todo move up
public class PermissionPrecondition extends Precondition {
  Permissible permissible;
  
  public PermissionPrecondition(@NotNull Permissible permissible) {
    this.permissible = permissible;
  }
  
  @Override
  public boolean canExecute(Sender sender) {
    String permission = permissible.getPermission();
    return permission == null || permission.isEmpty() || sender.hasPermission(permission) || sender.isOp();
  }
  
  @Override
  public boolean canSee(Sender sender) {
    return canExecute(sender);
  }
  
  @Override
  public String cannotExecuteFeedback(Sender sender) {
    return l10n.get("precondition.permission.cannotExecute", permissible.getPermission());
  }
}
