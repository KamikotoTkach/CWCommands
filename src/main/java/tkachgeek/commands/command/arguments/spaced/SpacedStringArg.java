package tkachgeek.commands.command.arguments.spaced;

import tkachgeek.commands.command.arguments.basic.StringArg;

public class SpacedStringArg extends StringArg implements SpacedArgument {
  public SpacedStringArg(String placeholder) {
    super(placeholder);
  }
  
  public SpacedStringArg() {
  }
}
