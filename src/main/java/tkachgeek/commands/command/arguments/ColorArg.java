package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.List;

public class ColorArg extends Argument {
   @Override
   public boolean valid(String raw) {
      return this.colors.contains(raw);
   }

   @Override
   public List<String> completions(CommandSender sender) {
      return this.colors;
   }

   @Override
   public String argumentName() {
      return "color";
   }

   private final List<String> colors = List.of("0123456789abcdef".split(""));
}
