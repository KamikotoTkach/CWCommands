package tkachgeek.commands.command;

import org.bukkit.command.CommandSender;

public interface Help {
  void sendTo(CommandSender sender, String[] args);
}
