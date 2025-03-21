package ru.cwcode.commands.permissions;

public class AllDeniedPermissionGenerationStrategy implements PermissionGenerationStrategy {
  final ProcessResult result = new ProcessResult("*", "*");
  
  @Override
  public ProcessResult processCommand(String currentPermission, String commandName) {
    return result;
  }
  
  @Override
  public ProcessResult processSubCommand(String previousPermission, String currentPermission, String subcommandName) {
    return result;
  }
  
  @Override
  public String processArgumentSet(String previousPermission, String argumentPermission, String subcommandPermission) {
    return "*";
  }
}
