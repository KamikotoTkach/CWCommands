package ru.cwcode.commands.permissions;

import org.jetbrains.annotations.NotNull;

public class DefaultPermissionGenerationStrategy implements PermissionGenerationStrategy {
  @Override
  public ProcessResult processCommand(String currentPermission, String commandName) {
    if (currentPermission == null) {
      return new ProcessResult(commandName);
    } else if (isStatic(currentPermission)) {
      return new ProcessResult(parseStatic(currentPermission), currentPermission);
    } else if (!currentPermission.isEmpty()) {
      return new ProcessResult(currentPermission);
    } else {
      return new ProcessResult("");
    }
  }
  
  @Override
  public ProcessResult processSubCommand(String previousPermissions, String currentPermission, String commandName) {
    if (isStatic(currentPermission)) {
      return new ProcessResult(parseStatic(currentPermission), currentPermission);
    }
    
    if (isStatic(previousPermissions)) {
      return new ProcessResult(parseStatic(previousPermissions), previousPermissions);
    }
    
    return new ProcessResult(previousPermissions + "." + currentPermission);
  }
  
  @Override
  public String processArgumentSet(String previousPermissions, String argumentPermission, String subCommandPermission) {
    if (isStatic(argumentPermission)) {
      return parseStatic(argumentPermission);
    }
    
    if (isStatic(previousPermissions)) {
      return parseStatic(previousPermissions);
    }
    
    if (notEmpty(argumentPermission)) {
      if (notEmpty(previousPermissions)) {
        return previousPermissions + "." + argumentPermission;
      } else {
        return argumentPermission;
      }
    } else {
      if (notEmpty(previousPermissions)) {
        return previousPermissions;
      } else {
        return "";
      }
    }
  }
  
  private static @NotNull String parseStatic(String staticPermission) {
    return staticPermission.substring(1);
  }
  
  private static boolean isStatic(String permission) {
    return permission != null && permission.startsWith("$");
  }
  
  private static boolean notEmpty(String permission) {
    return permission != null && !permission.isEmpty();
  }
}
