package tkachgeek.commands.command.arguments.basic;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Arrays;
import java.util.List;

public class BooleanArg extends Argument {
  private String trueValue;
  private String falseValue;
  
  public BooleanArg() {
    this("true", "false");
  }
  
  public BooleanArg(String trueValue, String falseValue) {
    this.trueValue = trueValue;
    this.falseValue = falseValue;
  }
  
  @Override
  public Integer toInt() {
    if (notNull()) return raw.equalsIgnoreCase(trueValue) ? 1 : 0;
    return null;
  }
  
  @Override
  public String toString() {
    if (notNull()) return raw.equalsIgnoreCase(trueValue) ? "true" : "false";
    return null;
  }
  
  @Override
  public Double toDouble() {
    if (notNull()) return raw.equalsIgnoreCase(trueValue) ? 1.0 : 0.0;
    return null;
  }
  
  @Override
  public Boolean toBoolean() {
    if (notNull()) return raw.equalsIgnoreCase(trueValue);
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
    return trueValue + "/" + falseValue;
  }
  
  @Override
  protected String hint() {
    return trueValue + " или " + falseValue;
  }
  
  public void setTrueValue(String trueValue) {
    this.trueValue = trueValue;
  }
  
  public void setFalseValue(String falseValue) {
    this.falseValue = falseValue;
  }
}
