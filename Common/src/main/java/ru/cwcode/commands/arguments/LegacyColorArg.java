package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.cwutils.colors.ColorUtils;

import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class LegacyColorArg extends Argument {
  
  @Override
  public boolean valid(String raw) {
    return ColorUtils.isColor(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return ColorUtils.getColors();
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.color.name");
  }
}
