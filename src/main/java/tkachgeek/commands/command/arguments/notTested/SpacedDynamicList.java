package tkachgeek.commands.command.arguments.notTested;

import tkachgeek.commands.command.Receiver;
import tkachgeek.commands.command.arguments.spaced.SpacedArgument;

public class SpacedDynamicList extends DynamicList implements SpacedArgument {
  public SpacedDynamicList(String name, Receiver receiver) {
    super(name, receiver);
  }
}
