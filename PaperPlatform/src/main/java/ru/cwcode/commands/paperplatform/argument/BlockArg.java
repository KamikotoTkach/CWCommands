package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Material;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockArg extends EnumArg {
  
  public BlockArg() {
    super((Enum[]) Arrays.stream(Material.values()).filter(Material::isBlock).toArray(), "блок");
  }
}
