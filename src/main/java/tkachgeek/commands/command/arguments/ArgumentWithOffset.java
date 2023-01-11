package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.List;

public abstract class ArgumentWithOffset extends Argument {
  int offset = 0;
  
  @Override
  public boolean valid(String raw) {
    return true;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return null;
  }
  
  public ArgumentWithOffset offset(int offset) {
    this.offset = offset;
    return this;
  }
  
  @Override
  public boolean valid(String raw, List<String> arguments) {
    return valid(raw, arguments.get(arguments.size() + offset - 1));
  }
  
  @Override
  public List<String> completions(CommandSender sender, List<String> written) {
    return completions(sender, written.get(written.size() + offset - 1));
  }
  
  public abstract boolean valid(String raw, String offsetValue);
  
  public abstract List<String> completions(CommandSender sender, String offsetValue);
}
