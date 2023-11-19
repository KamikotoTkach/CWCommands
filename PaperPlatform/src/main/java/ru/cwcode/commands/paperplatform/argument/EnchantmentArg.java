package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.enchantments.Enchantment;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantmentArg extends Argument {
  static List<String> enchants = Arrays.stream(Enchantment.values()).map(Enchantment::toString).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return enchants.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return enchants;
  }
  
  @Override
  public String argumentName() {
    return "зачарование";
  }
}
