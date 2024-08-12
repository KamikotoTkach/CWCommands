package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.potion.PotionEffectType;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

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
    return l10n.get("argument.potionEffect.name");
  }
  
  @Override
  public Object map() {
    return PotionEffectType.getByName(raw);
  }
}
