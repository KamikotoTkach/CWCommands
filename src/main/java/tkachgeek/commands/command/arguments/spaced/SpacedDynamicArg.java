package tkachgeek.commands.command.arguments.spaced;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.arguments.basic.DynamicArg;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SpacedDynamicArg extends DynamicArg implements SpacedArgument {
  public SpacedDynamicArg(String name, Function<CommandSender, List<String>> dynamicArgument, Predicate<String> predicate) {
    super(name, dynamicArgument, predicate);
  }
}
