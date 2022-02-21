package tkachgeek.commands.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OnlinePlayerWithPermissionArg extends Argument {
  String permission;
  String name = null;
  
  public OnlinePlayerWithPermissionArg(String permission) {
    this.permission = permission;
  }
  
  public OnlinePlayerWithPermissionArg(String permission, String name) {
    this.permission = permission;
    this.name = permission;
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
    return name == null ? "Игрок с " + permission : name;
  }
}
