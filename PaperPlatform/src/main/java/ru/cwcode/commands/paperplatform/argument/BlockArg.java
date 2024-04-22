package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Material;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockArg extends Argument {
  static List<String> blocks = Arrays.stream(Material.values()).filter(Material::isBlock).map(Enum::name).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return blocks.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return blocks;
  }
  
  @Override
  public String argumentName() {
    return "блок";
  }
  
  @Override
  public Material map() {
    return Material.matchMaterial(raw);
  }
}
