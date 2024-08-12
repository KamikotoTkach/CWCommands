package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Collection;
import java.util.List;

public abstract class BindedArgument extends Argument {
  int bind = 0;
  
  @Override
  public boolean valid(String raw) {
    return true;
  }
  
  @Override
  public Collection<String> completions(Sender sender) {
    return null;
  }
  
  public BindedArgument bind(int bind) {
    this.bind = bind;
    return this;
  }
  
  @Override
  public boolean valid(Sender sender, String raw, List<String> arguments) {
    return valid(raw, arguments.get(bind));
  }
  
  @Override
  public Collection<String> completions(Sender sender, List<String> written) {
    return completions(sender, written.get(bind));
  }
  
  /**
   * @param raw    written value
   * @param binded binded value
   */
  public abstract boolean valid(String raw, String binded);
  
  
  /**
   * @param sender command sender
   * @param binded binded value
   */
  public abstract Collection<String> completions(Sender sender, String binded);
}
