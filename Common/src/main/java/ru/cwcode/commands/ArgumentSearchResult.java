package ru.cwcode.commands;

import org.jetbrains.annotations.Nullable;
import ru.cwcode.commands.arguments.ExactStringArg;

import java.util.ArrayList;
import java.util.List;

public class ArgumentSearchResult {
  List<ArgumentFitnessResult> notValid = new ArrayList<>();
  @Nullable ArgumentSet founded = null;
  @Nullable String errorMessage = null;
  
  public ArgumentSearchResult founded(ArgumentSet set) {
    founded = set;
    return this;
  }
  
  public @Nullable String getErrorMessage() {
    return errorMessage;
  }
  
  public void add(ArgumentFitnessResult fitnessResult) {
    if (fitnessResult.getArgumentSet() == null) return;
    if (fitnessResult.getInvalidArgument() instanceof ExactStringArg) return;
    
    notValid.add(fitnessResult);
  }
  
  public boolean isFounded() {
    return getFounded() != null;
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
  
  public ArgumentSearchResult error(ArgumentSet set, String errorMessage) {
    this.founded = set;
    this.errorMessage = errorMessage;
    return this;
  }
}
