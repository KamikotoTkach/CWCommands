package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;
import tkachgeek.commands.command.Argument;

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
  public List<String> completions(CommandSender sender) {
    return effects;
  }
  
  @Override
  public String argumentName() {
    return "Эффект зелья";
  }
}
