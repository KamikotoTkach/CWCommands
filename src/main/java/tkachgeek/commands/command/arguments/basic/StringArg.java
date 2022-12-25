package tkachgeek.commands.command.arguments.basic;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.CompletionStyle;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class StringArg extends Argument {
  static final int INITIAL_MAX_STRING_LENGTH = 10000;
  protected String placeholder = "текст";
  int min = 1;
  int max = INITIAL_MAX_STRING_LENGTH;
  CompletionStyle style = CompletionStyle.PLACEHOLDER;
  Pattern pattern;
  
  public StringArg(String placeholder) {
    this.placeholder = placeholder;
  }
  
  public StringArg() {
  }
  
  public StringArg setMin(int min) {
    this.min = min;
    return this;
  }
  
  public StringArg setMax(int max) {
    this.max = max;
    return this;
  }
  
  public StringArg setStyle(CompletionStyle style) {
    this.style = style;
    return this;
  }
  
  public StringArg setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
    return this;
  }
  
  public StringArg setPattern(Pattern pattern) {
    this.pattern = pattern;
    return this;
  }
  
  @Override
  public boolean valid(String raw) {
    if (raw.length() < min || raw.length() > max) return false;
    return pattern == null || pattern.matcher(raw).matches();
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    switch (style) {
      case PLACEHOLDER -> {
        return Collections.singletonList(placeholder);
      }
      case DIAPASON -> {
        return Collections.singletonList("Строка от " + min + " до " + max + " символов " + (pattern == null ? "" : pattern.toString()));
      }
    }
    return Collections.emptyList();
  }
  
  @Override
  public String argumentName() {
    return placeholder;
  }
  
  @Override
  protected String hint() {
    if(pattern != null) return pattern.pattern();
    
    StringBuilder builder = new StringBuilder();
    builder.append("От ").append(min);
    if (max != INITIAL_MAX_STRING_LENGTH) builder.append(" до ").append(max);
    builder.append(" символов");
    return builder.toString();
  }
}