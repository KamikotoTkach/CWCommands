package tkachgeek.commands.command.arguments.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import tkachgeek.commands.command.Argument;

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
  public List<String> completions(CommandSender sender) {
    return enchants;
  }
  
  @Override
  public String argumentName() {
    return "зачарование";
  }
}
