package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.enchantments.Enchantment;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class EnchantmentArg extends Argument {
  static Set<String> enchants = Arrays.stream(Enchantment.values())
                                      .map(x->x.getKey().value())
                                      .collect(Collectors.toSet());
  
  @Override
  public boolean valid(String raw) {
    return enchants.contains(raw);
  }
  
  @Override
  public Set<String> completions(Sender sender) {
    return enchants;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.enchantment.name");
  }
  
  @Override
  public Object map() {
    return Enchantment.getByName(raw);
  }
}
