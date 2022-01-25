package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import tkachgeek.commands.command.Argument;

import java.util.Arrays;
import java.util.List;

public class EnchantmentArg extends Argument {
  static List<String> enchants = Arrays.stream(Enchantment.values()).map(x->x.getKey().value()).toList();
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
    return "Название зачарования";
  }
}
