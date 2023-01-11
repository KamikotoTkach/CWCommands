package tkachgeek.commands.command.arguments.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayerWithPermissionArg extends Argument {
  String permission;
  String argumentName = null;
  
  public OnlinePlayerWithPermissionArg(String permission) {
    this.permission = permission;
  }
  
  public OnlinePlayerWithPermissionArg(String permission, String argumentName) {
    this.permission = permission;
    this.argumentName = permission;
  }
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getPlayer(raw) != null && Bukkit.getPlayer(raw).hasPermission(permission) || Bukkit.getPlayer(raw).isOp() && permission.equals("*");
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    List<String> list = new ArrayList<>();
    for (Player x : Bukkit.getOnlinePlayers()) {
      if (x.hasPermission(permission)) {
        list.add(x.getName());
      }
    }
    return list;
  }
  
  @Override
  public String argumentName() {
    return argumentName == null ? "Игрок с " + permission : argumentName;
  }
}
