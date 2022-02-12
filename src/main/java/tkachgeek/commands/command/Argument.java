package tkachgeek.commands.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class Argument {
  protected String raw;
  private boolean optional;
  
  protected Argument(String raw) {
    this.raw = raw;
  }
  
  public Argument() {
  
  }
  
  public abstract boolean valid(String raw);
  
  public boolean valid(String raw, List<String> arguments) {
    return valid(raw);
  }
  
  public abstract List<String> completions(CommandSender sender);
  
  public List<String> completions(CommandSender sender, List<String> written) {
    return completions(sender);
  }
  
  public Argument optional() {
    optional = true;
    return this;
  }
  
  public abstract String argumentName();
  
  public boolean valid() {
    return raw != null && valid(raw);
  }
  
  public Integer toInt() {
    try {
      if (valid()) return Integer.parseInt(raw);
    } catch (Exception exception) {
      Bukkit.getLogger().warning("Не удалось преобразовать `" + raw + "` в int в " + argumentName());
    }
    return null;
  }
  
  public Float toFloat() {
    try {
      if (valid()) return Float.parseFloat(raw);
    } catch (Exception exception) {
      Bukkit.getLogger().warning("Не удалось преобразовать `" + raw + "` в float в " + argumentName());
    }
    
    return null;
  }
  
  public String toString() {
    if (valid()) return raw;
    Bukkit.getLogger().warning("Не удалось преобразовать `" + (raw != null ? raw : "~null~") + "` в строку в " + argumentName());
    return null;
  }
  
  public Double toDouble() {
    try {
      if (valid()) return Double.parseDouble(raw);
    } catch (Exception exception) {
      Bukkit.getLogger().warning("Не удалось преобразовать `" + raw + "` в double в " + argumentName());
    }
    return null;
  }
  
  public Boolean toBoolean() {
    try {
      if (valid()) return Boolean.parseBoolean(raw);
    } catch (Exception exception) {
      Bukkit.getLogger().warning("Не удалось преобразовать `" + raw + "` в boolean в " + argumentName());
    }
    
    return null;
  }
  
  public boolean isOptional() {
    return optional;
  }
  
  public boolean isPresented() {
    return raw != null;
  }
}
