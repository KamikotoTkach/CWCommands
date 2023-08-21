package ru.cwcode.commands;

import ru.cwcode.commands.api.CommandsAPI;

public enum DebugMode {
  NONE, REDUCED, DETAILED;
  
  public void print(String text) {
    CommandsAPI.getPlatform().getLogger().warning(text);
  }
  
  public boolean is(DebugMode mode) {
    return this.ordinal() >= mode.ordinal();
  }
}
