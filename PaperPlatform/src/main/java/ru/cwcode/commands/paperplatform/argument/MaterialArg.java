package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Material;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MaterialArg extends Argument {
  static List<String> materials = Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return materials.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return materials;
  }
  
  @Override
  public String argumentName() {
    return "материал";
  }
  
  @Override
  public Material map() {
    return Material.matchMaterial(raw);
  }
}
