package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.Receiver;

import java.util.List;

public class DynamicList extends Argument {
  private final String name;
  Receiver receiver;
  
  public DynamicList(String name, Receiver receiver) {
    this.receiver = receiver;
    this.name = name;
  }
  
  @Override
  public boolean valid(String raw) {
    return receiver.receive().contains(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return receiver.receive();
  }
  
  @Override
  public String argumentName() {
    return name;
  }
}
