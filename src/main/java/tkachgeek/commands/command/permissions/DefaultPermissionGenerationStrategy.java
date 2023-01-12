package tkachgeek.commands.command.permissions;

public class DefaultPermissionGenerationStrategy implements PermissionGenerationStrategy {
   @Override
   public ProcessResult processCommand(String currentPermission, String commandName) {
      if (currentPermission == null) {
         return new ProcessResult(commandName);
      } else if (currentPermission.startsWith("$")) {
         ProcessResult result = new ProcessResult(currentPermission);
         result.setPermission(currentPermission.substring(1));
         return result;
      } else if (!currentPermission.isEmpty()) {
         return new ProcessResult(currentPermission);
      } else {
         return new ProcessResult("");
      }
   }

   @Override
   public ProcessResult processSubCommand(String previousPermissions, String currentPermission, String commandName) {
      if (currentPermission.startsWith("$")) {
         ProcessResult result = new ProcessResult(currentPermission.substring(1), previousPermissions);
         return result;
      }

      if (previousPermissions.startsWith("$")) {
         ProcessResult result = new ProcessResult(previousPermissions);
         result.setPermission(previousPermissions.substring(1));
         return result;
      }

      if (currentPermission == null) {
         return new ProcessResult(previousPermissions + "." + commandName);
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
