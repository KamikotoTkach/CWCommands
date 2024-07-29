package ru.cwcode.commands.extra.builder;

import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.commands.extra.command.executor.ExtraExecutor;

import java.util.function.BiConsumer;

public class DeleteCommandBuilder<E, K, S extends Sender> extends CommandBuilder<E, K, S> {
  BiConsumer<S, E> onExecute;
  
  public DeleteCommandBuilder(RepositoryAccessor<E, K, S> builder) {
    super(builder);
  }
  
  public DeleteCommandBuilder<E, K, S> onExecute(BiConsumer<S, E> onExecute) {
    this.onExecute = onExecute;
    return this;
  }
  
  @Override
  public void register(Command command) {
    command.arguments(
      new ArgumentSet(new ExtraExecutor<>(repositoryAccessor, (s, e, __) -> onExecute.accept(s, e), 1),
                      new ExactStringArg("delete"),
                      repositoryAccessor.keyArgument()));
  }
}
