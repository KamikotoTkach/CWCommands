package ru.cwcode.commands.arguments.basic;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.CompletionStyle;
import ru.cwcode.commands.api.Sender;
import tkachgeek.tkachutils.numbers.NumbersUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerArg extends Argument {
  int min = Integer.MIN_VALUE;
  int max = Integer.MAX_VALUE;
  CompletionStyle style = CompletionStyle.PLACEHOLDER;
  String placeholder = "Целое число";
  
  public IntegerArg() {
  }
  
  public IntegerArg(String placeholder) {
    this.placeholder = placeholder;
  }
  
  public IntegerArg setMin(int min) {
    this.min = min;
    return this;
  }
  
  public IntegerArg setMax(int max) {
    this.max = max;
    return this;
  }
  
  public IntegerArg setStyle(CompletionStyle style) {
    this.style = style;
    return this;
  }
  
  public IntegerArg setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
    return this;
  }
  
  @Override
  public boolean valid(String raw) {
    try {
      if (!NumbersUtils.isInteger(raw)) return false;
      
      int parsed = Integer.parseInt(raw);
      return parsed >= min && parsed < max;
    } catch (NumberFormatException ignored) {
    }
    return false;
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return switch (style) {
      case PLACEHOLDER -> Collections.singletonList(placeholder);
      case DIAPASON -> Collections.singletonList(min + " -> " + max);
      case LIST -> //todo: add cache
         IntStream.range(min, max).limit(1000).mapToObj(Integer::toString).collect(Collectors.toList());
      default -> Collections.emptyList();
    };
  }
  
  @Override
  public String argumentName() {
    return placeholder;
  }
  
  @Override
  protected String hint() {
    StringBuilder builder = new StringBuilder();
    
    boolean minFlag = min != Integer.MIN_VALUE;
    if (minFlag) {
      builder.append("От ").append(min);
    }
    if (max != Integer.MAX_VALUE) builder.append(minFlag ? " до " : "До ").append(max);
    
    return builder.toString();
  }
}
