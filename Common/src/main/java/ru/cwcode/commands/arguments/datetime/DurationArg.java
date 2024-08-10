package ru.cwcode.commands.arguments.datetime;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.cwutils.datetime.StringToDuration;

import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class DurationArg extends Argument {
  
  private List<String> completions = List.of(l10n.get("argument.duration.completions.y"),
                                             l10n.get("argument.duration.completions.M"),
                                             l10n.get("argument.duration.completions.w"),
                                             l10n.get("argument.duration.completions.d"),
                                             l10n.get("argument.duration.completions.h"),
                                             l10n.get("argument.duration.completions.m"),
                                             l10n.get("argument.duration.completions.s"),
                                             l10n.get("argument.duration.completions.t"));
  
  @Override
  public boolean valid(String raw) {
    return StringToDuration.isValid(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return completions;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.duration.name");
  }
  
  @Override
  protected String hint() {
    return l10n.get("argument.duration.hint");
  }
  
  @Override
  public Object map() {
    return StringToDuration.parse(raw);
  }
}
