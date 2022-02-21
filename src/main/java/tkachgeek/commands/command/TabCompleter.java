package tkachgeek.commands.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
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
    //Benchmark.stage("TabComplete", "searchedCommand");
    
    final List<String> complete = new ArrayList<>();
    
    final AbstractMap.SimpleEntry<Command, Integer> parseResult = CommandParser.parse(this.command, sender, args);
    
    //Benchmark.stage("TabComplete", "copyedArgs");
    
    final Command foundedCommand = parseResult.getKey();
    final int deep = parseResult.getValue();
    
    if (args.length > deep) {
      args = Arrays.copyOfRange(args, deep, args.length);
    } else {
      return complete;
    }
    
    //Benchmark.stage("TabComplete", "addedSubcommands");
    
    final List<String> arguments = Arrays.asList(args);
    
    if (arguments.size() == 1) {
      for (Command cmd : foundedCommand.getSubcommandsFor(sender)) {
        complete.add(cmd.name);
      }
    }
    
    //Benchmark.stage("TabComplete", "addedArgumentSets");
    
    for (ArgumentSet set : foundedCommand.getArgumentSetsFor(sender)) {
      complete.addAll(set.getCompletesFor(arguments, sender));
    }
    
    //Benchmark.stage("TabComplete", "gettedSuggestions");
    
    List<String> suggestions = StringUtils.getSuggestions(complete, arguments.get(arguments.size() - 1), maxLinesPerCompletions);
    for (String suggestion : suggestions) {
      sender.sendMessage(suggestion);
    }
    //Benchmark.newIteration("TabComplete");
    return suggestions;
  }
}
