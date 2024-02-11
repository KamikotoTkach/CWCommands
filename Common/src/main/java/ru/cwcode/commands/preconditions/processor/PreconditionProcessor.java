package ru.cwcode.commands.preconditions.processor;

import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.preconditions.Precondition;

import java.util.Collection;

public class PreconditionProcessor {
  public static PreconditionResult process(Sender sender, Collection<Precondition> preconditions) {
    var feedback = new PreconditionResult();
    
    for (Precondition precondition : preconditions) {
      if (feedback.canPerform() && !precondition.canExecute(sender)) {
        feedback.setCanPerform(false);
        feedback.setCannotPerformMessage(precondition.cannotExecuteFeedback(sender));
      }
      
      if (feedback.canSee() && !precondition.canSee(sender)) {
        feedback.setCanSee(false);
      }
    }
    
    return feedback;
  }
}
