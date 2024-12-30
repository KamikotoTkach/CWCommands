package ru.cwcode.commands.extra.builder;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.commands.extra.command.executor.SimpleExecutor;

import java.util.function.BiConsumer;

public class CreateCommandBuilder<E, K, S extends Sender> extends CommandBuilder<E, K, S> {
  BiConsumer<ArgumentParser, S> onExecute;
  String name = "create";
  
  private Argument[] arguments;
  
  public CreateCommandBuilder(RepositoryAccessor<E, K, S> builder) {
    super(builder);
  }
  
  public CreateCommandBuilder<E, K, S> name(String name) {
    this.name = name;
    return this;
  }
  
  public <A extends Argument> CreateCommandBuilder<E, K, S> fields(A... arguments) {
    this.arguments = arguments;
    return this;
  }
  
  public CreateCommandBuilder<E, K, S> onExecute(BiConsumer<ArgumentParser, S> onExecute) {
    this.onExecute = onExecute;
    return this;
  }
  
  @Override
  public void register(Command command) {
    command.arguments(
       new ArgumentSet(new SimpleExecutor<S>((s, argumentParser) -> onExecute.accept(argumentParser, s)),
                       new ExactStringArg(name),
                       arguments));
  }
}
