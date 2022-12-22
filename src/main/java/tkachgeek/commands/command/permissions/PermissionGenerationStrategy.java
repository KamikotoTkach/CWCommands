package tkachgeek.commands.command.permissions;

public interface PermissionGenerationStrategy {
  ProcessResult processCommand(String currentPermission, String commandName);
  ProcessResult processSubCommand(String previousPermission, String currentPermission, String subcommandName);
  String processArgumentSet(String previousPermission, String argumentPermission, String subcommandPermission);
}
