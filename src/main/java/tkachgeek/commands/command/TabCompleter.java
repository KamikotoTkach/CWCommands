package tkachgeek.commands.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tkachgeek.tkachutils.benchmark.Benchmark;
import tkachgeek.tkachutils.text.StringUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
  private static int maxLinesPerCompletions = 64;
  private final Command command;
  
  public TabCompleter(Command command) {
    this.command = command;
  }
  
  public static void setMaxCompletionsCount(int maxLinesPerCompletions) {
    TabCompleter.maxLinesPerCompletions = maxLinesPerCompletions;
  }
  
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, String[] args) {
    final List<String> complete = new ArrayList<>();
    
    final AbstractMap.SimpleEntry<Command, Integer> parseResult = CommandParser.parse(this.command, sender, args);
    
    final Command foundedCommand = parseResult.getKey();
    final int deep = parseResult.getValue();
    
    if (args.length > deep) {
      args = Arrays.copyOfRange(args, deep, args.length);
    } else {
      return complete;
    }
    
    final List<String> arguments = Arrays.asList(args);
  
    if (arguments.size() == 1) {
      for (Command cmd : foundedCommand.getSubcommandsFor(sender)) {
        complete.add(cmd.name);
      }
    }
    
    for (ArgumentSet set : foundedCommand.getArgumentSetsFor(sender)) {
      complete.addAll(set.getCompletesFor(arguments, sender));
    }
  
    return StringUtils.getSuggestions(complete, arguments.get(arguments.size() - 1), maxLinesPerCompletions);
  }
}
