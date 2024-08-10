package ru.cwcode.commands.arguments.basic;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.CompletionStyle;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.cwutils.numbers.NumbersUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class DoubleArg extends Argument {
  double min = -Double.MAX_VALUE;
  double max = Double.MAX_VALUE;
  CompletionStyle style = CompletionStyle.PLACEHOLDER;
  double step = 1.0;
  String placeholder = l10n.get("argument.double.placeholder");
  
  public DoubleArg(String placeholder) {
    this.placeholder = placeholder;
  }
  
  public DoubleArg() {
  }
  
  public DoubleArg setMin(double min) {
    this.min = min;
    return this;
  }
  
  public DoubleArg setMax(double max) {
    this.max = max;
    return this;
  }
  
  public DoubleArg setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
    return this;
  }
  
  public DoubleArg setStyle(CompletionStyle style) {
    this.style = style;
    return this;
  }
  
  public DoubleArg setStep(double step) {
    this.step = step;
    return this;
  }
  
  public DoubleArg positive() {
    this.min = 0;
    return this;
  }
  
  @Override
  public boolean valid(String raw) {
    try {
      if (!NumbersUtils.isNumber(raw)) return false;
      
      double parsed = Double.parseDouble(raw);
      return parsed >= min && parsed < max;
    } catch (NumberFormatException ignored) {
    }
    return false;
  }
  
  @Override
  public List<String> completions(Sender sender) {
    switch (style) {
      case PLACEHOLDER:
        return Collections.singletonList(placeholder);
      case DIAPASON:
        return Collections.singletonList(min + " -> " + max);
      case LIST:
        return DoubleStream.iterate(this.min, d -> d <= max, d -> d + step)
                           .limit(1000)
                           .mapToObj(Double::toString).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
  
  @Override
  public String argumentName() {
    return placeholder;
  }
  
  @Override
  public Object map() {
    return toDouble();
  }
  
  @Override
  protected String hint() {
    
    boolean minFlag = min > Double.MIN_VALUE;
    boolean maxFlag = max < Double.MAX_VALUE;
    
    if (minFlag && maxFlag) return l10n.get("argument.double.hint.minmax", min, max);
    if (!minFlag && maxFlag) return l10n.get("argument.double.hint.max", max);
    if (minFlag && !maxFlag) return l10n.get("argument.double.hint.min", min);
    
    return placeholder;
  }
}
