package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Collections;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

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
    return l10n.get("argument.empty.name");
  }
}
