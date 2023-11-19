package ru.cwcode.commands.permissions;

public interface PermissionGenerationStrategy {
  PermissionGenerationStrategy ALL_ALLOWED = new PermissionGenerationStrategy() {
    final ProcessResult result = new ProcessResult("", "");
    
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
      return "";
    }
  };
  PermissionGenerationStrategy ALL_DENIED = new PermissionGenerationStrategy() {
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
  };
  
  ProcessResult processCommand(String currentPermission, String commandName);
  
  ProcessResult processSubCommand(String previousPermission, String currentPermission, String subcommandName);
  
  String processArgumentSet(String previousPermission, String argumentPermission, String subcommandPermission);
}
