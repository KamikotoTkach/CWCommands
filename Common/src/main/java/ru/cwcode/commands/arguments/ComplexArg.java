package ru.cwcode.commands.arguments;


import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.List;

public class ComplexArg extends Argument {

  List<Argument> args;
  
  public ComplexArg(Argument... args) {
    this.args = List.of(args);
  }
  
  public List<Argument> getArgs() {
    return args;
  }
  
  @Override
  public boolean valid(String raw) {
    return false;
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return null;
  }
  
  @Override
  public String argumentName() {
    return null;
  }
}
