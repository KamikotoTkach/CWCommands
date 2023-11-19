package ru.cwcode.commands.arguments.spaced;

import ru.cwcode.commands.arguments.SafetyStringArg;

public class SafetySpacedStringArg extends SafetyStringArg implements SpacedArgument {
  public SafetySpacedStringArg(String placeholder) {
    super(placeholder);
  }
  
  public SafetySpacedStringArg() {
  }
}
