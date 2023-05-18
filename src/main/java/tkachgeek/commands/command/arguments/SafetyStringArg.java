package tkachgeek.commands.command.arguments;

import tkachgeek.commands.command.arguments.basic.StringArg;
import tkachgeek.tkachutils.text.StringUtils;

public class SafetyStringArg extends StringArg {
  public SafetyStringArg() {
  
  }
  
  public SafetyStringArg(String placeholder) {
    this.placeholder = placeholder;
  }
  
  @Override
  public boolean valid(String raw) {
    return super.valid(raw) && StringUtils.isSafetyString(raw);
  }
}
