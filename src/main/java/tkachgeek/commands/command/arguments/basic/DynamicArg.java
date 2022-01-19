package tkachgeek.commands.command.arguments.basic;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
@Deprecated
public class DynamicArg extends Argument {
  final Function<CommandSender, List<String>> dynamicArgument;
  final Predicate<String> predicate;
  private final String name;
  
  public DynamicArg(String name, Function<CommandSender, List<String>> dynamicArgument, Predicate<String> predicate) {
    this.dynamicArgument = dynamicArgument;
    this.predicate = predicate;
    this.name = name;
  }
  
  @Override
  public Boolean toBoolean() {
    return Boolean.parseBoolean(raw);
  }
  
  @Override
  public boolean valid(String raw) {
    return predicate.test(raw);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return dynamicArgument.apply(sender);
  }
  
  @Override
  public String argumentName() {
    return name;
  }
}
