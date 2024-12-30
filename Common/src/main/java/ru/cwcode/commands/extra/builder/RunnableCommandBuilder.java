package ru.cwcode.commands.extra.builder;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.commands.extra.TriConsumer;
import ru.cwcode.commands.extra.command.executor.ExtraExecutor;
import ru.cwcode.cwutils.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class RunnableCommandBuilder<E, K, S extends Sender> extends CommandBuilder<E, K, S> {
  TriConsumer<S, E, ArgumentParser> onExecute;
  String subcommandName = "run";
  List<Argument> arguments = new ArrayList<>();
  
  public RunnableCommandBuilder(RepositoryAccessor<E, K, S> builder) {
    super(builder);
  }
  
  public RunnableCommandBuilder<E, K, S> withSubcommandName(String subcommandName) {
    this.subcommandName = subcommandName;
    return this;
  }
  
  public RunnableCommandBuilder<E, K, S> runnable(TriConsumer<S, E, ArgumentParser> onExecute) {
    this.onExecute = onExecute;
    return this;
  }
  
  public RunnableCommandBuilder<E, K, S> withArguments(Argument... arguments) {
    this.arguments = List.of(arguments);
    return this;
  }
  
  @Override
  public void register(Command command) {
    Argument[] args = CollectionUtils.combine(List.of(new ExactStringArg(subcommandName),
                                                      repositoryAccessor.keyArgument()),
                                              arguments).toArray(Argument[]::new);
    
    command.arguments(
      new ArgumentSet(
        new ExtraExecutor<>(repositoryAccessor, (sender, element, parser) -> onExecute.accept(sender, element, parser), 1)
        , args)
    );
  }
}
