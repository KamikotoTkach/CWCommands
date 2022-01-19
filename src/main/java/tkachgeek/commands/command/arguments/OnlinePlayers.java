package tkachgeek.commands.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import tkachgeek.commands.command.Argument;

import java.util.List;

public class OnlinePlayers extends Argument {
  public OnlinePlayers() {
  }
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).anyMatch(x -> x.equalsIgnoreCase(raw));
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
  }
  
  @Override
  public String argumentName() {
    return "Онлайн игрок";
  }
}
