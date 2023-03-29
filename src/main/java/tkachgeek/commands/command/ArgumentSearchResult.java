package tkachgeek.commands.command;

import tkachgeek.commands.command.arguments.ExactStringArg;

import java.util.ArrayList;
import java.util.List;

public class ArgumentSearchResult {
  List<ArgumentFitnessResult> notValid = new ArrayList<>();
  ArgumentSet founded = null;
  
  public ArgumentSearchResult founded(ArgumentSet set) {
    founded = set;
    return this;
  }
  
  public void add(ArgumentFitnessResult fitnessResult) {
    if (fitnessResult.getArgumentSet() == null) return;
    if (fitnessResult.getInvalidArgument() instanceof ExactStringArg) return;
    
    notValid.add(fitnessResult);
  }
  
  public boolean isFounded() {
    return founded != null;
  }
  
  public boolean canShowDetailedHelp() {
    return !notValid.isEmpty();
  }
  
  public List<ArgumentFitnessResult> getInvalidResults() {
    return notValid;
  }
  
  public ArgumentSet getFounded() {
    return founded;
  }
}
