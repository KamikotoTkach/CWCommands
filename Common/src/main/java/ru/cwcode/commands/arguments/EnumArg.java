package ru.cwcode.commands.arguments;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArg extends Argument {
  private final List<String> values = new ArrayList<>();
  private final String name;
  
  public EnumArg(Enum[] values, String name) {
    this.name = name;
    this.values.addAll(Stream.of(values)
                             .map(Enum::name)
                             .collect(Collectors.toList()));
  }
  
  @Override
  public boolean valid(String s) {
    return values.contains(s);
  }
  
  @Override
  public List<String> completions(Sender commandSender) {
    return values;
  }
  
  @Override
  public String argumentName() {
    return name;
  }
}
