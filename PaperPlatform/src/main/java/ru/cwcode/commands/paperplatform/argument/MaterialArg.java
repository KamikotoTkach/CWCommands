package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Material;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

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
    return l10n.get("argument.material.name");
  }
  
  @Override
  public Material map() {
    return Material.matchMaterial(raw);
  }
}
