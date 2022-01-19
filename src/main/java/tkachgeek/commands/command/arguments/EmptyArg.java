package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Collections;
import java.util.List;

public class EmptyArg extends Argument {
  @Override
  public boolean valid(String raw) {
    return true;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return Collections.singletonList(argumentName());
  }
  
  @Override
  public String argumentName() {
    return "Любое значение";
  }
}
