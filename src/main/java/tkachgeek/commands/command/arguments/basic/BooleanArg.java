package tkachgeek.commands.command.arguments.basic;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Arrays;
import java.util.List;

public class BooleanArg extends Argument {
  final String trueValue;
  final String falseValue;
  
  public BooleanArg() {
    this("true", "false");
  }
  
  public BooleanArg(String trueValue, String falseValue) {
    this.trueValue = trueValue;
    this.falseValue = falseValue;
  }
  
  @Override
  public Integer toInt() {
    if(valid()) return raw.equalsIgnoreCase(trueValue)?1:0;
    return null;
  }
  
  @Override
  public String toString() {
    if(valid()) return raw.equalsIgnoreCase(trueValue)?"true":"false";
    return null;
  }
  
  @Override
  public Double toDouble() {
    if(valid()) return raw.equalsIgnoreCase(trueValue)?1.0:0.0;
    return null;
  }
  
  @Override
  public Boolean toBoolean() {
    if(valid()) return raw.equalsIgnoreCase(trueValue);
    return null;
  }
  
  @Override
  public boolean valid(String raw) {
    return raw.equalsIgnoreCase(trueValue) || raw.equalsIgnoreCase(falseValue);
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return Arrays.asList(trueValue, falseValue);
  }
  
  @Override
  public String argumentName() {
    return "Логическое значение";
  }
}
