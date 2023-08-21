package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;

public class ListArg extends Argument {
  final List<String> acceptableStrings;
  private final String name;
  
  public ListArg(String name, List<String> acceptableStrings) {
    this.name = name;
    this.acceptableStrings = acceptableStrings;
  }
  
  public ListArg(String name, String... acceptableStrings) {
    this.name = name;
    this.acceptableStrings = Arrays.asList(acceptableStrings);
  }
  
  @Override
  public boolean valid(String raw) {
    return acceptableStrings.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return acceptableStrings;
  }
  
  @Override
  public String argumentName() {
    return name;
  }
}
