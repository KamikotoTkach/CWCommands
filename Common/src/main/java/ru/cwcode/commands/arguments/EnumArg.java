package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArg extends Argument {
  private final String name;
  private Map<String, Enum> values;
  
  public EnumArg(Enum[] values, String name) {
    this.name = name;
    this.values = Stream.of(values).collect(Collectors.toMap(Enum::name,
                                                             anEnum -> anEnum,
                                                             (o, o2) -> o,
                                                             HashMap::new));
  }
  
  @Override
  public boolean valid(String s) {
    return values.containsKey(s);
  }
  
  @Override
  public Set<String> completions(Sender commandSender) {
    return values.keySet();
  }
  
  @Override
  public Object map() {
    return values.get(raw);
  }
  
  @Override
  public String argumentName() {
    return name;
  }
}
