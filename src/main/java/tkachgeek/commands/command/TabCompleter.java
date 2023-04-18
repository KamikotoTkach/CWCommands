package tkachgeek.commands.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tkachgeek.tkachutils.text.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
  private final Command command;
  private int maxLinesPerCompletions = 64;
  
  public TabCompleter(Command command) {
    this.command = command;
  }
  
  public void setMaxCompletionsEntries(int maxLinesPerCompletions) {
    this.maxLinesPerCompletions = maxLinesPerCompletions;
  }
  
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, String[] args) {
    //Benchmark.stage("TabComplete", "searchedCommand");
    
    final List<String> complete = new ArrayList<>();
    
    final CommandParser.Result parseResult = CommandParser.parse(this.command, sender, args);
    
    //Benchmark.stage("TabComplete", "copyedArgs");
    
    final Command foundedCommand = parseResult.getCommand();
    final int deep = parseResult.getDeep();
    
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
        complete.addAll(cmd.aliases);
      }
    }
    
    //Benchmark.stage("TabComplete", "addedArgumentSets");
    
    for (ArgumentSet set : foundedCommand.getArgumentSetsFor(sender)) {
      complete.addAll(set.getCompletesFor(arguments, sender));
    }
    
    //Benchmark.stage("TabComplete", "gettedSuggestions");
    
    List<String> suggestions = StringUtils.getSuggestions(complete, arguments.get(arguments.size() - 1), maxLinesPerCompletions);
    
    //for (String suggestion : suggestions) {
    //  sender.sendMessage(suggestion);
    //}
    //Benchmark.newIteration("TabComplete");
    return suggestions;
  }
}
