package ru.cwcode.commands.arguments.spaced;

import ru.cwcode.commands.arguments.ListArg;

import java.util.List;

public class SpacedListArg extends ListArg implements SpacedArgument {
  public SpacedListArg(String name, List<String> acceptableStrings) {
    super(name, acceptableStrings);
  }
  
  public SpacedListArg(String name, String... acceptableStrings) {
    super(name, acceptableStrings);
  }
}
