package tkachgeek.commands.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tkachgeek.commands.command.Argument;

import java.util.ArrayList;
import java.util.List;

public class WorldArg extends Argument {
  
  @NotNull
  private static List<String> getWorlds() {
    List<String> list = new ArrayList<>();
    for (World world : Bukkit.getWorlds()) {
      list.add(world.getName());
    }
    return list;
  }
  
  @Override
  public boolean valid(String raw) {
    return getWorlds().contains(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return getWorlds();
  }
  
  @Override
  public String argumentName() {
    return "мир";
  }
}
