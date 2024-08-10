package ru.cwcode.commands.paperplatform.argument;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class PlayerArg extends OnlinePlayers {
  @Override
  public boolean valid(String raw) {
    return raw.matches("^\\w{3,16}$");
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
