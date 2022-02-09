package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimeArg extends Argument {
  static List<AbstractMap.SimpleEntry<String, List<String>>> completions = new ArrayList<>() {{
    add(new AbstractMap.SimpleEntry<>("", Arrays.asList("0", "1", "2")));
    add(new AbstractMap.SimpleEntry<>("^[0-2:]", Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")));
    add(new AbstractMap.SimpleEntry<>("^(([0,1][0-9])|(2[0-3]))", Arrays.asList(":")));
    add(new AbstractMap.SimpleEntry<>("^(([0,1][0-9])|(2[0-3])):", Arrays.asList("0", "1", "2", "3", "4", "5")));
    add(new AbstractMap.SimpleEntry<>("^(([0,1][0-9])|(2[0-3])):[0-5]", Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")));
  }};
  
  @Override
  public boolean valid(String raw) {
    return raw.matches("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$");
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return null;
  }
  
  @Override
  public List<String> completions(CommandSender sender, List<String> written) {
    String last = written.get(written.size() - 1);
    
    if (last.length() >= 5 || !last.matches(completions.get(last.length()).getKey())) {
      return new ArrayList<>();
    }
    return completions.get(last.length()).getValue().stream().map(x -> last + x).collect(Collectors.toList());
  }
  
  @Override
  public String argumentName() {
    return "Время";
  }
}
