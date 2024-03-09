package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Material;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MaterialArg extends EnumArg {
  
  public MaterialArg() {
    super(Material.values(), "материал");
  }
  public MaterialArg(Predicate<Material> predicate) {
    super((Enum[]) Arrays.stream(Material.values()).filter(predicate).toArray(), "материал");
  }
}
