package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.List;

public class WorldArg extends Argument {
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getWorld(raw) != null;
  }
  
  @Override
  public List<String> completions(Sender sender) {
    List<String> list = new ArrayList<>();
    for (World world : Bukkit.getWorlds()) {
      list.add(world.getName());
    }
    return list;
  }
  
  @Override
  public String argumentName() {
    return "мир";
  }
  
  @Override
  public Object map() {
    return Bukkit.getWorld(raw);
  }
}
