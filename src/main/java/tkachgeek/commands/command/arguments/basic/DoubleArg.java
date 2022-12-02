package tkachgeek.commands.command.arguments.basic;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.CompletionStyle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DoubleArg extends Argument {
  double min = Double.MIN_VALUE;
  double max = Double.MAX_VALUE;
  CompletionStyle style = CompletionStyle.PLACEHOLDER;
  double step = 1.0;
  
  public DoubleArg setMin(double min) {
    this.min = min;
    return this;
  }
  
  public DoubleArg setMax(double max) {
    this.max = max;
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
  
  String placeholder = "Дробное число";
  
  public DoubleArg(String placeholder) {
   this.placeholder = placeholder;
  }
  
  public DoubleArg() {
  }
  
  @Override
  public boolean valid(String raw) {
    try {
      double parsed = Double.parseDouble(raw);
      return parsed >= min && parsed <= max;
    } catch (NumberFormatException ignored) {
    }
    return false;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    switch (style) {
      case PLACEHOLDER -> {
        return Collections.singletonList(placeholder);
      }
      case DIAPASON -> {
        return Collections.singletonList(min + " -> " + max);
      }
      case LIST -> {
        return DoubleStream.iterate(min, d -> d <= max, d -> d + step)
           .limit(1000)
           .mapToObj(Double::toString).collect(Collectors.toList());
      }
    }
    return Collections.emptyList();
  }
  
  @Override
  public String argumentName() {
    return placeholder;
  }
}
