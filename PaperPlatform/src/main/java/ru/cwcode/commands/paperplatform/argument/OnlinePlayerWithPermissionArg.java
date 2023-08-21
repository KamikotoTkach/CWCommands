package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

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
    Player player = Bukkit.getPlayer(raw);
    return player != null && player.hasPermission(permission) || player.isOp() && permission.equals("*");
  }
  
  @Override
  public List<String> completions(Sender sender) {
    List<String> list = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission(permission)) {
        list.add(player.getName());
      }
    }
    return list;
  }
  
  @Override
  public String argumentName() {
    return argumentName == null ? "Игрок с " + permission : argumentName;
  }
}
