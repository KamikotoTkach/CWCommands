package tkachgeek.commands.command.arguments;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialArg extends Argument {
  static List<String> materials = Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return materials.contains(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return materials;
  }
  
  @Override
  public String argumentName() {
    return "Материал";
  }
}
