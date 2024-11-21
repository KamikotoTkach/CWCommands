package ru.cwcode.commands.preconditions;

import org.jetbrains.annotations.Nullable;

public class PreconditionResult {
  private boolean canPerform = true;
  private boolean canSee = true;
  private @Nullable String cannotPerformMessage = null;
  
  PreconditionResult() {
  }
  
  public PreconditionResult(boolean canPerform, boolean canSee, @Nullable String cannotPerformMessage) {
    this.canPerform = canPerform;
    this.canSee = canSee;
    this.cannotPerformMessage = cannotPerformMessage;
  }
  
  public boolean canPerform() {
    return canPerform;
  }
  
  public boolean canSee() {
    return canSee;
  }
  
  public @Nullable String getCannotPerformMessage() {
    return cannotPerformMessage;
  }
  
  void setCanPerform(boolean canPerform) {
    this.canPerform = canPerform;
  }
  
  void setCanSee(boolean canSee) {
    this.canSee = canSee;
  }
  
  void setCannotPerformMessage(@Nullable String cannotPerformMessage) {
    this.cannotPerformMessage = cannotPerformMessage;
  }
  
  public boolean isSatisfy(PreconditionRequirements requirements) {
    return (canPerform || !requirements.canPerform()) && (canSee || !requirements.canSee());
  }
}
