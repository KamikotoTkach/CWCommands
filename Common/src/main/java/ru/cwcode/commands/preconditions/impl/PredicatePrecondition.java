package ru.cwcode.commands.preconditions.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.preconditions.Precondition;

import java.util.function.Predicate;

public class PredicatePrecondition implements Precondition {
  @NotNull
  Predicate<Sender> predicate;
  @Nullable
  String cannotExecuteFeedback = null;
  
  public PredicatePrecondition(@NotNull Predicate<Sender> predicate, @Nullable String cannotExecuteFeedback) {
    this.predicate = predicate;
    this.cannotExecuteFeedback = cannotExecuteFeedback;
  }
  
  public PredicatePrecondition(@NotNull Predicate<Sender> predicate) {
    this.predicate = predicate;
  }
  
  @Override
  public boolean canExecute(Sender sender) {
    return predicate.test(sender);
  }
  
  @Override
  public boolean canSee(Sender sender) {
    return cannotExecuteFeedback != null || canExecute(sender);
  }
  
  @Override
  public String cannotExecuteFeedback(Sender sender) {
    return cannotExecuteFeedback;
  }
}
