package tkachgeek.commands.command.permissions;

public class ProcessResult {
  String permission;
  String nextPermissions;
  
  public ProcessResult(String permission, String nextPermissions) {
    this.permission = permission;
    this.nextPermissions = nextPermissions;
  }
  
  public ProcessResult(String permission) {
    this.permission = permission;
    this.nextPermissions = permission;
  }
  
  public ProcessResult() {
  }
  
  public String getPermission() {
    return permission;
  }
  
  public String getNextPermissions() {
    return nextPermissions;
  }
  
  public void setPermission(String permission) {
    this.permission = permission;
  }
  
  public void setNextPermissions(String nextPermissions) {
    this.nextPermissions = nextPermissions;
  }
}
