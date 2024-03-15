package ru.cwcode.commands.paperplatform.argument;


public class PlayerArg extends OnlinePlayers {
  @Override
  public boolean valid(String raw) {
    return raw.matches("^\\w{3,16}$");
  }
  
  @Override
  public String argumentName() {
    return "игрок";
  }
  
  @Override
  public String map() {
    return raw;
  }
}
