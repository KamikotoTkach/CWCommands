package tkachgeek.commands.command.arguments.spaced;

import tkachgeek.commands.command.arguments.SafetyStringArg;

public class SafetySpacedStringArg extends SafetyStringArg implements SpacedArgument {
   public SafetySpacedStringArg(String placeholder) {
      super(placeholder);
   }

   public SafetySpacedStringArg() {
   }
}
