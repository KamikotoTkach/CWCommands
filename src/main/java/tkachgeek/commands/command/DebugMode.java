package tkachgeek.commands.command;

import org.bukkit.Bukkit;

public enum DebugMode {
  NONE, MAIN, DETAILED;
  
  public void print(String text) {
      Bukkit.broadcastMessage(text);
    }
  
  public boolean is(DebugMode mode) {
    return this.ordinal()>= mode.ordinal();
  }
}
