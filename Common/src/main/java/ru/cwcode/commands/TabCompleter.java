package ru.cwcode.commands;

import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.preconditions.processor.PreconditionRequirements;
import ru.cwcode.cwutils.text.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class TabCompleter {
  private final Command command;
  private int maxLinesPerCompletions = 64;
  
  public TabCompleter(Command command) {
    this.command = command;
  }
  
  public void setMaxCompletionsEntries(int maxLinesPerCompletions) {
    this.maxLinesPerCompletions = maxLinesPerCompletions;
  }
  
  public List<Completion> onTabComplete(@NotNull Sender sender, String[] args) {
    //Benchmark.stage("TabComplete", "searchedCommand");
    
    final List<Completion> suggestions = new ArrayList<>();
    
    final CommandParser.Result parseResult = CommandParser.parse(this.command, sender, args);
    
    //Benchmark.stage("TabComplete", "copyedArgs");
    
    final Command foundedCommand = parseResult.getCommand();
    final int deep = parseResult.getDeep();
    
    if (args.length > deep) {
      args = Arrays.copyOfRange(args, deep, args.length);
    } else {
      return suggestions;
    }
    
    //Benchmark.stage("TabComplete", "addedSubcommands");
    
    final List<String> arguments = Arrays.asList(args);
    
    if (arguments.size() == 1) {
      for (Command cmd : foundedCommand.getSubcommandsFor(sender, PreconditionRequirements.CAN_PERFORM_AND_CAN_SEE)) {
        suggestions.add(new Completion(cmd)); //todo: в статик метод Suggestion
        suggestions.addAll(cmd.aliases.stream()
                                      .map(x -> new Completion(x, cmd.description(), cmd.getColorScheme()))
                                      .collect(Collectors.toList()));
      }
    }
    
    //Benchmark.stage("TabComplete", "addedArgumentSets");
    
    for (ArgumentSet set : foundedCommand.getArgumentSetsFor(sender, PreconditionRequirements.CAN_PERFORM_AND_CAN_SEE)) {
      suggestions.addAll(set.getCompletesFor(arguments, sender, command));
    }
    
    //Benchmark.stage("TabComplete", "gettedSuggestions");
    
    HashSet<String> filteredCompletions = new HashSet<>(StringUtils.getSuggestions(suggestions.stream().map(Completion::suggestion).collect(Collectors.toList()), arguments.get(arguments.size() - 1), maxLinesPerCompletions));
    List<Completion> filteredSuggestions = suggestions.stream().filter(x -> filteredCompletions.contains(x.suggestion())).collect(Collectors.toList());
    
    //for (String suggestion : suggestions) {
    //  sender.sendMessage(suggestion);
    //}
    //Benchmark.newIteration("TabComplete");
    return filteredSuggestions;
  }
}
