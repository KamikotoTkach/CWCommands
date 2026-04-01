package ru.cwcode.commands.paperplatform.argument;

import ru.cwcode.commands.api.Sender;

import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class PlayerArg extends OnlinePlayers {
  @Override
  public boolean valid(String raw) {
    return raw.matches("^\\w{3,16}$");
  }
  
  @Override
  public boolean valid(Sender sender, String raw, List<String> arguments) {
    return valid(raw);
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.player.name");
  }
  
  @Override
  public String map() {
    return raw;
  }
}
