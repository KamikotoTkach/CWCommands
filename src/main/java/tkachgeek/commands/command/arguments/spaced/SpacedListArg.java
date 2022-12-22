package tkachgeek.commands.command.arguments.spaced;

import tkachgeek.commands.command.arguments.ListArg;

import java.util.List;

public class SpacedListArg extends ListArg implements SpacedArgument {
  public SpacedListArg(String name, List<String> acceptableStrings) {
    super(name, acceptableStrings);
  }
  
  public SpacedListArg(String name, String... acceptableStrings) {
    super(name, acceptableStrings);
  }
}
