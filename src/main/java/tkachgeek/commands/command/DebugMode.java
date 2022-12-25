package tkachgeek.commands.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public enum DebugMode {
  NONE, MAIN, DETAILED;
  
  public void print(String text) {
      Bukkit.broadcast(Component.text(text));
    }
  
  public boolean is(DebugMode mode) {
    return this.ordinal()>= mode.ordinal();
  }
}
