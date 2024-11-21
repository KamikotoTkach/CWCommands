package ru.cwcode.commands.preconditions;

public class PreconditionRequirements {
  public static PreconditionRequirements ONLY_CAN_PERFORM = new PreconditionRequirements(true, false);
  public static PreconditionRequirements ONLY_CAN_SEE = new PreconditionRequirements(false, true);
  public static PreconditionRequirements CAN_PERFORM_AND_CAN_SEE = new PreconditionRequirements(true, true);
  private final boolean canPerform;
  private final boolean canSee;
  
  private PreconditionRequirements(boolean canPerform, boolean canSee) {
    this.canPerform = canPerform;
    this.canSee = canSee;
  }
  
  public boolean canPerform() {
    return canPerform;
  }
  
  public boolean canSee() {
    return canSee;
  }
}
