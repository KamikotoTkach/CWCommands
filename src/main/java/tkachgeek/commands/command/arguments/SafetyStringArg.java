package tkachgeek.commands.command.arguments;

import tkachgeek.commands.command.arguments.basic.StringArg;
import tkachgeek.tkachutils.text.StringUtils;

public class SafetyStringArg extends StringArg {
   @Override
   public boolean valid(String raw) {
      return StringUtils.isSafetyString(raw);
   }

   public SafetyStringArg() {

   }

   public SafetyStringArg(String placeholder) {
      this.placeholder = placeholder;
   }
}
