package ru.cwcode.commands.arguments;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Collections;
import java.util.List;

public class EmptyArg extends Argument {
  @Override
  public boolean valid(String raw) {
    return true;
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return Collections.singletonList(argumentName());
  }
  
  @Override
  public String argumentName() {
    return "любое значение";
  }
}
