package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.potion.PotionEffectType;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PotionEffectArg extends Argument {
  static Set<String> effects = Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toSet());
  
  @Override
  public boolean valid(String raw) {
    return effects.contains(raw);
  }
  
  @Override
  public Set<String> completions(Sender sender) {
    return effects;
  }
  
  @Override
  public String argumentName() {
    return "эффект";
  }
  
  @Override
  public Object map() {
    return PotionEffectType.getByName(raw);
  }
}
