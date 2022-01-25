package tkachgeek.commands.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.List;

public class WorldArg extends Argument {
  static List<String> worlds = Bukkit.getWorlds().stream().map(World::getName).toList();
  
  @Override
  public boolean valid(String raw) {
    return worlds.contains(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return worlds;
  }
  
  @Override
  public String argumentName() {
    return "Название мира";
  }
}
