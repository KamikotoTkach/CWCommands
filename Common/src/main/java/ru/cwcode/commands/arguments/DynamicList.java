package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.Receiver;
import ru.cwcode.commands.api.Sender;

import java.util.List;

public class DynamicList extends Argument {
  private final String name;
  Receiver receiver;
  
  public DynamicList(String name, Receiver receiver) {
    this.receiver = receiver;
    this.name = name;
  }
  
  @Override
  public boolean valid(String raw) {
    return receiver.receive().contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return receiver.receive();
  }
  
  @Override
  public String argumentName() {
    return name;
  }
}
