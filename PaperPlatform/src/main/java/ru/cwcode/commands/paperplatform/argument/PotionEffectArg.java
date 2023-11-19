package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.potion.PotionEffectType;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PotionEffectArg extends Argument {
  static List<String> effects = Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return effects.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return effects;
  }
  
  @Override
  public String argumentName() {
    return "эффект";
  }
}
