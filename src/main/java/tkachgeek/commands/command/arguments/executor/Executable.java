package tkachgeek.commands.command.arguments.executor;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.ArgumentSet;

@FunctionalInterface
public interface Executable {
  void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet);
}
