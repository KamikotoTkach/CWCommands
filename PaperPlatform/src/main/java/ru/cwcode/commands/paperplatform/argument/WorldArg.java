package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

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
    return l10n.get("argument.world.name");
  }
  
  @Override
  public Object map() {
    return Bukkit.getWorld(raw);
  }
}
