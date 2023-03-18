package tkachgeek.commands.command.arguments.bukkit;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.List;

public class LegacyColorArg extends Argument {
  private static final List<String> colors = List.of("0123456789abcdef".split(""));
  
  @Override
  public boolean valid(String raw) {
    return colors.contains(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return colors;
  }
  
  @Override
  public String argumentName() {
    return "цвет";
  }
}
