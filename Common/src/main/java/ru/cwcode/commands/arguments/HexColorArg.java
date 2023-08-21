package ru.cwcode.commands.arguments;

import ru.cwcode.commands.arguments.basic.StringArg;

import java.util.regex.Pattern;

public class HexColorArg extends StringArg {
  public HexColorArg() {
    setPattern(Pattern.compile("#[0-9A-F]{6}|#[0-9A-F]{3}", Pattern.CASE_INSENSITIVE));
  }
  
  public HexColorArg(String placeholder) {
    this();
    this.placeholder = placeholder;
  }
  
  @Override
  public String argumentName() {
    return "HEX";
  }
}
