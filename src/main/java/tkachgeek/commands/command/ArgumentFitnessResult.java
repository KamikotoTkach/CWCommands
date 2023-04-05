package tkachgeek.commands.command;

public class ArgumentFitnessResult {
  public static ArgumentFitnessResult SUCCESS = new ArgumentFitnessResult();
  public static ArgumentFitnessResult NOT_FIT = new ArgumentFitnessResult();
  private ArgumentSet argumentSet = null;
  private Argument invalidArgument = null;
  private String invalidStringArgument = null;
  
  public ArgumentFitnessResult(ArgumentSet argumentSet, Argument invalidArgument, String invalidStringArgument) {
    this.invalidArgument = invalidArgument;
    this.invalidStringArgument = invalidStringArgument;
    this.argumentSet = argumentSet;
  }
  
  public ArgumentFitnessResult() {
  }
  
  public boolean success() {
    return this.equals(SUCCESS);
  }
  
  public ArgumentSet getArgumentSet() {
    return argumentSet;
  }
  
  public Argument getInvalidArgument() {
    return invalidArgument;
  }
  
  public String getInvalidStringArgument() {
    return invalidStringArgument;
  }
}
