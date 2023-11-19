package ru.cwcode.commands.arguments.spaced;

import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.basic.DynamicArg;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SpacedDynamicArg extends DynamicArg implements SpacedArgument {
  public SpacedDynamicArg(String name, Function<Sender, List<String>> dynamicArgument, Predicate<String> predicate) {
    super(name, dynamicArgument, predicate);
  }
}
