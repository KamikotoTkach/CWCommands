package ru.cwcode.commands.permissions;

public class DefaultPermissionGenerationStrategy implements PermissionGenerationStrategy {
  @Override
  public ProcessResult processCommand(String currentPermission, String commandName) {
    if (currentPermission == null) {
      return new ProcessResult(commandName);
    } else if (currentPermission.startsWith("$")) {
      return new ProcessResult(currentPermission.substring(1), currentPermission);
    } else if (!currentPermission.isEmpty()) {
      return new ProcessResult(currentPermission);
    } else {
      return new ProcessResult("");
    }
  }
  
  @Override
  public ProcessResult processSubCommand(String previousPermissions, String currentPermission, String commandName) {
    if (currentPermission.startsWith("$")) {
      return new ProcessResult(currentPermission.substring(1), currentPermission);
    }
    
    if (previousPermissions.startsWith("$")) {
      return new ProcessResult(previousPermissions.substring(1), previousPermissions);
    }
    
    return new ProcessResult(previousPermissions + "." + currentPermission);
  }
  
  @Override
  public String processArgumentSet(String previousPermissions, String argumentPermission, String subCommandPermission) {
    if (previousPermissions.startsWith("$")) {
      return previousPermissions.substring(1);
    }
    
    if (argumentPermission != null && !argumentPermission.isEmpty()) {
      if (!previousPermissions.isEmpty()) {
        return previousPermissions + "." + argumentPermission;
      } else {
        return argumentPermission;
      }
    } else {
      if (!previousPermissions.isEmpty()) {
        return previousPermissions;
      } else {
        return "";
      }
    }
  }
}
