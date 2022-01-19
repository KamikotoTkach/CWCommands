package tkachgeek.commands.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TabCompleter implements org.bukkit.command.TabCompleter {
  private static int maxLinesPerCompletions = Integer.MAX_VALUE;
  private final Command command;
  
  public TabCompleter(Command command) {
    this.command = command;
  }
  
  public static void setMaxCompletionsCount(int maxLinesPerCompletions) {
    TabCompleter.maxLinesPerCompletions = maxLinesPerCompletions;
  }
  
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, String[] args) {
    AbstractMap.SimpleEntry<Command, Integer> parseResult = CommandParser.parse(this.command, sender, args);
    
    Command foundedCommand = parseResult.getKey();
    int deep = parseResult.getValue();
    
    args = args.length > deep ? Arrays.copyOfRange(args, deep, args.length) : new String[]{};
    List<String> complete = new ArrayList<>();
    if(args.length == 0) return complete;
    
    List<String> arguments = new ArrayList<>(Arrays.asList(args));
    if (foundedCommand != null) {
      if (arguments.size() == 1) {
        complete.addAll(foundedCommand.getSubcommandsFor(sender).stream()
           .map(cmd -> cmd.name)
           .filter(cmdName -> cmdName.toLowerCase().startsWith(arguments.get(0).toLowerCase()))
           .toList());
      }
      foundedCommand.getArgumentSetsFor(sender).stream()
         .flatMap(set -> set.getCompletesFor(arguments, sender).stream())
         .map(x -> new AbstractMap.SimpleEntry<>(x, 0))
         .peek(entry -> {
           if (arguments.get(0).length() == 0) {
             entry.setValue(1);
             return;
           }
           int score = 0;
           int commandCharPointer = 0;
           int argumentCharPointer = 0;
           boolean applyMultiplier = true;
           while (commandCharPointer < entry.getKey().length() && argumentCharPointer < arguments.get(0).length()) {
             if (entry.getKey().toLowerCase().charAt(commandCharPointer++) == arguments.get(0).toLowerCase().charAt(argumentCharPointer)) {
               score++;
               argumentCharPointer++;
               if (applyMultiplier) score += 3;
               applyMultiplier = true;
             } else {
               applyMultiplier = false;
             }
           }
           if (argumentCharPointer != arguments.get(0).length()) {
             score /= (arguments.get(0).length() - argumentCharPointer) + 1;
           }
           entry.setValue(score);
         })
         //.filter(x -> x.getValue() > 0)
         //.parallel()
         .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
         .forEach(x -> complete.add(x.getKey()));
    }
    return complete;
  }
}
