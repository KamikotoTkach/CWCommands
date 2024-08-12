package ru.cwcode.commands.arguments.datetime;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class TimeArg extends Argument {
  static List<AbstractMap.SimpleEntry<String, List<String>>> completions = List.of(
     new AbstractMap.SimpleEntry<>("", List.of("0", "1", "2")),
     new AbstractMap.SimpleEntry<>("^[0-2:]", List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
     new AbstractMap.SimpleEntry<>("^(([0,1][0-9])|(2[0-3]))", List.of(":")),
     new AbstractMap.SimpleEntry<>("^(([0,1][0-9])|(2[0-3])):", List.of("0", "1", "2", "3", "4", "5")),
     new AbstractMap.SimpleEntry<>("^(([0,1][0-9])|(2[0-3])):[0-5]", List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
  );
  
  @Override
  public boolean valid(String raw) {
    return raw.matches("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$");
  }
  
  @Override
  public List<String> completions(Sender sender, List<String> written) {
    String last = written.get(written.size() - 1);
    
    if (last.length() >= 5 || !last.matches(completions.get(last.length()).getKey())) {
      return Collections.EMPTY_LIST;
    }
    return completions.get(last.length()).getValue().stream().map(x -> last + x).collect(Collectors.toList());
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return null;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.time.name");
  }
  
  @Override
  public String hint() {
    return l10n.get("argument.time.hint");
  }
}
