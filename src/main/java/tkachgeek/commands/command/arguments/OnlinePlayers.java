package tkachgeek.commands.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayers extends Argument {
  public OnlinePlayers() {
  }
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getPlayer(raw) != null;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    List<String> list = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      list.add(player.getName());
    }
    return list;
  }
  
  @Override
  public String argumentName() {
    return "Онлайн игрок";
  }
}
