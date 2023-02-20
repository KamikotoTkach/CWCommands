package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.List;

public abstract class BindedArgument extends Argument {
  int bind = 0;
  
  @Override
  public boolean valid(String raw) {
    return true;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return null;
  }
  
  public BindedArgument bind(int bind) {
    this.bind = bind;
    return this;
  }
  
  @Override
  public boolean valid(CommandSender sender, String raw, List<String> arguments) {
    return valid(raw, arguments.get(bind));
  }
  
  @Override
  public List<String> completions(CommandSender sender, List<String> written) {
    return completions(sender, written.get(bind));
  }
  
  public abstract boolean valid(String raw, String offsetValue);
  
  public abstract List<String> completions(CommandSender sender, String offsetValue);
}
