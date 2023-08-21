package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.CommandParser;

public class PaperCommandParser extends CommandParser implements CommandExecutor {
  public PaperCommandParser(Command command) {
    super(command);
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    return onCommandExecute(new PaperSender(commandSender),s,strings);
  }
}
