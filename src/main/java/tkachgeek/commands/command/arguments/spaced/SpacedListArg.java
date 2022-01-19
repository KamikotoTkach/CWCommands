package tkachgeek.commands.command.arguments.spaced;

import tkachgeek.commands.command.arguments.ListArg;

import java.util.List;

public class SpacedListArg extends ListArg implements SpacedArgument {
  private SpacedListArg(){}
  
  public SpacedListArg(List<String> acceptable) {
    super(acceptable);
  }
  
  public SpacedListArg(String... acceptable) {
    super(acceptable);
  }
}
