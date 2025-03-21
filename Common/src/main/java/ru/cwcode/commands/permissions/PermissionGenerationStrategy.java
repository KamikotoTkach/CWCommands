package ru.cwcode.commands.permissions;

public interface PermissionGenerationStrategy {
  PermissionGenerationStrategy ALL_ALLOWED = new AllAllowedPermissionGenerationStrategy();
  PermissionGenerationStrategy ALL_DENIED = new AllDeniedPermissionGenerationStrategy();
  
  ProcessResult processCommand(String currentPermission, String commandName);
  
  ProcessResult processSubCommand(String previousPermission, String currentPermission, String subcommandName);
  
  String processArgumentSet(String previousPermission, String argumentPermission, String subcommandPermission);
}
