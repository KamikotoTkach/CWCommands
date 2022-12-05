package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Collections;
import java.util.List;

public class ExactStringArg extends Argument {
  final String exactString;
  
  public String getExactString() {
    return exactString;
  }
  
  public ExactStringArg(String exactString) {
    this.exactString = exactString;
  }
  
  @Override
  public boolean valid(String raw) {
    return exactString.equalsIgnoreCase(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return Collections.singletonList(exactString);
  }
  
  @Override
  public String argumentName() {
    return exactString;
  }
}

