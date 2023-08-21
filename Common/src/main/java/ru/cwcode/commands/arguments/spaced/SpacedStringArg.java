package ru.cwcode.commands.arguments.spaced;

import ru.cwcode.commands.arguments.basic.StringArg;

public class SpacedStringArg extends StringArg implements SpacedArgument {
  public SpacedStringArg(String placeholder) {
    super(placeholder);
  }
  
  public SpacedStringArg() {
  }
}
