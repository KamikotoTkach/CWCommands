package tkachgeek.commands.command.arguments.bukkit;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.tkachutils.colors.ColorUtils;

import java.util.List;

public class LegacyColorArg extends Argument {

   @Override
   public boolean valid(String raw) {
      return ColorUtils.isColor(raw);
   }

   @Override
   public List<String> completions(CommandSender sender) {
      return ColorUtils.getColors();
   }

   @Override
   public String argumentName() {
      return "цвет";
   }
}
