package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.Completion;
import ru.cwcode.commands.TabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public class PaperTabCompleter extends TabCompleter implements org.bukkit.command.TabCompleter {
  public PaperTabCompleter(Command command) {
    super(command);
  }
  
  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    return onTabComplete(new PaperSender(commandSender), strings).stream()
                                                                 .map(Completion::suggestion)
                                                                 .collect(Collectors.toList());
  }
}
