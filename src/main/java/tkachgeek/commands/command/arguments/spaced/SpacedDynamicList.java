package tkachgeek.commands.command.arguments.spaced;

import tkachgeek.commands.command.Receiver;
import tkachgeek.commands.command.arguments.DynamicList;

public class SpacedDynamicList extends DynamicList implements SpacedArgument {
  public SpacedDynamicList(String name, Receiver receiver) {
    super(name, receiver);
  }
}
