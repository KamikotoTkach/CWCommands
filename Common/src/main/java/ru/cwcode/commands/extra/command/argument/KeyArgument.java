package ru.cwcode.commands.extra.command.argument;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.RepositoryAccessor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KeyArgument<E, K, S extends Sender> extends Argument {
  private final RepositoryAccessor<E, K, S> repositoryAccessor;
  
  public KeyArgument(RepositoryAccessor<E, K, S> repositoryAccessor) {
    this.repositoryAccessor = repositoryAccessor;
  }
  
  @Override
  public boolean valid(String raw) {
    return false;
  }
  
  @Override
  public boolean valid(Sender sender, String raw, List<String> arguments) {
    return repositoryAccessor.keys((S) sender).contains(repositoryAccessor.stringToKey(raw));
  }
  
  @Override
  public Collection<String> completions(Sender sender) {
    return repositoryAccessor.keys((S) sender).stream()
                             .map(repositoryAccessor::keyToString)
                             .collect(Collectors.toList());
  }
  
  @Override
  public String argumentName() {
    return "ID";
  }
}
