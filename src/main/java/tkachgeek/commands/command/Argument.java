package tkachgeek.commands.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tkachgeek.commands.command.arguments.ExactStringArg;

import java.util.List;

public abstract class Argument {
  protected String raw;
  private boolean optional;
  private String _default = null;
  
  protected Argument(String raw) {
    this.raw = raw;
  }
  
  public Argument() {
  
  }
  
  @NotNull
  public TextComponent toComponent() {
    if (isOptional()) {
      return Component.text("[" + argumentName() + "]", Command.argumentOptional);
    } else if (this instanceof ExactStringArg) {
      return Component.text(argumentName(), Command.subcommandColor);
    } else {
      TextComponent component = Component.text("<" + argumentName() + ">", Command.argument);
      if (hint().isEmpty()) {
        return component;
      }
      return component.hoverEvent(HoverEvent.showText(Component.text(hint(), Command.comment)));
    }
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
  
  public Argument optional(String _default) {
    this._default = _default;
    return optional();
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
  
  public String getDefault() {
    return _default;
  }
  
  protected String hint() {
    return "";
  }
}
