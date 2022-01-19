package tkachgeek.commands.command.arguments.spaced;

import tkachgeek.commands.command.CompletionStyle;
import tkachgeek.commands.command.arguments.basic.StringArg;

import java.util.regex.Pattern;

public class SpacedStringArg extends StringArg implements SpacedArgument {
  public SpacedStringArg(int minLen, int maxLen) {
    super(minLen, maxLen);
  }
  
  public SpacedStringArg(int minLen, int maxLen, CompletionStyle completionStyle) {
    super(minLen, maxLen, completionStyle);
  }
  
  public SpacedStringArg() {
    super();
  }
  
  public SpacedStringArg(Pattern pattern) {
    super(pattern);
  }
  
  public SpacedStringArg(String placeholder) {
    super(placeholder);
  }
}
