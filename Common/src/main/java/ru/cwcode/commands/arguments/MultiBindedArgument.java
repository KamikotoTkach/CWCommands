package ru.cwcode.commands.arguments;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Collection;
import java.util.List;

public abstract class MultiBindedArgument extends Argument {
  int[] binds;

  @Override
  public boolean valid(String raw) {
    return true;
  }

  @Override
  public Collection<String> completions(Sender sender) {
    return null;
  }

  public MultiBindedArgument bind(int... binds) {
    this.binds = binds;
    return this;
  }

  @Override
  public boolean valid(Sender sender, String raw, List<String> arguments) {
    String[] offsetValues = new String[binds.length];

    for (int i = 0; i < binds.length; i++) {
      offsetValues[i] = arguments.get(binds[i]);
    }

    return valid(raw, offsetValues);
  }

  @Override
  public Collection<String> completions(Sender sender, List<String> written) {
    String[] offsetValues = new String[binds.length];

    for (int i = 0; i < binds.length; i++) {
      offsetValues[i] = written.get(binds[i]);
    }

    return completions(sender, offsetValues);
  }

  /**
   * @param raw written value
   * @param binded binded values
   */
  public abstract boolean valid(String raw, String[] binded);

  /**
   * @param sender command sender
   * @param binded binded values
   */
  public abstract Collection<String> completions(Sender sender, String[] binded);
}
