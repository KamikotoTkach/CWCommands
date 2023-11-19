package ru.cwcode.commands.arguments.spaced;

import ru.cwcode.commands.Receiver;
import ru.cwcode.commands.arguments.DynamicList;

public class SpacedDynamicList extends DynamicList implements SpacedArgument {
  public SpacedDynamicList(String name, Receiver receiver) {
    super(name, receiver);
  }
}
