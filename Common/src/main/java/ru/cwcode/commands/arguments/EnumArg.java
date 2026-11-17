package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArg extends Argument {
  private final String name;
  private final Map<String, Enum> values;
  private final Map<String, Enum> valuesIgnoringCase;

  public EnumArg(Enum[] values, String name) {
    this.name = name;
    this.values = Stream.of(values).collect(Collectors.toMap(Enum::name,
                                                             anEnum -> anEnum,
                                                             (o, o2) -> o,
                                                             HashMap::new));
    this.valuesIgnoringCase = Stream.of(values).collect(Collectors.toMap(anEnum -> lowerCase(anEnum.name()),
                                                                         anEnum -> anEnum,
                                                                         (o, o2) -> o,
                                                                         HashMap::new));
  }

  @Override
  public boolean valid(String s) {
    return find(s) != null;
  }

  @Override
  public Set<String> completions(Sender commandSender) {
    return values.keySet();
  }

  @Override
  public Object map() {
    return find(raw);
  }

  @Override
  public String argumentName() {
    return name;
  }

  private Enum find(String written) {
    if (written == null) return null;

    Enum exact = values.get(written);
    return exact != null ? exact : valuesIgnoringCase.get(lowerCase(written));
  }

  private static String lowerCase(String value) {
    return value.toLowerCase(Locale.ROOT);
  }
}
