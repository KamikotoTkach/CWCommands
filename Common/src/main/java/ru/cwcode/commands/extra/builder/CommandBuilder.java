package ru.cwcode.commands.extra.builder;

import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.RepositoryAccessor;

public abstract class CommandBuilder<E, K, S extends Sender> {
  protected RepositoryAccessor<E, K, S> repositoryAccessor;
  
  public CommandBuilder(RepositoryAccessor<E, K, S> repositoryAccessor) {
    this.repositoryAccessor = repositoryAccessor;
  }
  
  public abstract void register(Command command);
}
