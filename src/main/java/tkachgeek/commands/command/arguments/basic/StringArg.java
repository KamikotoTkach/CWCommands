package tkachgeek.commands.command.arguments.basic;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.CompletionStyle;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class StringArg extends Argument {
  int min = 1;
  int max = 10000;
  CompletionStyle style = CompletionStyle.PLACEHOLDER;
  protected String placeholder = "Произвольная строка";
  Pattern pattern;
  
  public StringArg(int minLen, int maxLen) {
    this.min = minLen;
    this.max = maxLen;
  }
  
  public StringArg(int minLen, int maxLen, CompletionStyle completionStyle) {
    this(minLen, maxLen);
    this.style = completionStyle;
  }
  
  public StringArg() {
  
  }
  
  public StringArg(Pattern pattern) {
    this.pattern = pattern;
  }
  
  public StringArg(String placeholder) {
    this.placeholder = placeholder;
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
}