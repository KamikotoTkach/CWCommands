package ru.cwcode.commands.extra;

import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.builder.CommandBuilder;
import ru.cwcode.commands.extra.builder.CreateCommandBuilder;
import ru.cwcode.commands.extra.builder.DeleteCommandBuilder;
import ru.cwcode.commands.extra.builder.RunnableCommandBuilder;
import ru.cwcode.commands.extra.builder.list.ListCommandBuilder;
import ru.cwcode.commands.extra.builder.update.UpdateCommandBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExtraCommandBuilder<E, K, S extends Sender> {
  
  private final RepositoryAccessor<E, K, S> repositoryAccessor;
  private final List<CommandBuilder<E, K, S>> builders = new ArrayList<>();
  
  public ExtraCommandBuilder(RepositoryAccessor<E, K, S> repositoryAccessor) {
    this.repositoryAccessor = repositoryAccessor;
  }
  
  public static <E, S extends Sender> ExtraCommandBuilder<E, String, S> of(BiFunction<E, S, String> elementToKey,
                                                                           BiFunction<String, S, E> keyToElement,
                                                                           Function<S, Collection<String>> keys) {
    
    return new ExtraCommandBuilder<>(
       new RepositoryAccessor<>(elementToKey, keyToElement, s -> s, s -> s, keys)
    );
  }
  
  public static <E, K, S extends Sender> ExtraCommandBuilder<E, K, S> of(BiFunction<E, S, K> elementToKey,
                                                                         BiFunction<K, S, E> keyToElement,
                                                                         Function<K, String> keyToString,
                                                                         Function<String, K> stringToKey,
                                                                         Function<S, Collection<K>> keys) {
    
    return new ExtraCommandBuilder<>(
       new RepositoryAccessor<>(elementToKey, keyToElement, keyToString, stringToKey, keys)
    );
  }
  
  public <B extends CommandBuilder<E, K, S>>
  ExtraCommandBuilder<E, K, S> builder(Function<RepositoryAccessor<E, K, S>, B> builderCreator, Consumer<B> builder) {
    
    B b = builderCreator.apply(repositoryAccessor);
    
    builder.accept(b);
    builders.add(b);
    
    return this;
  }
  
  public ExtraCommandBuilder<E, K, S> delete(Consumer<DeleteCommandBuilder<E, K, S>> builder) {
    return builder(DeleteCommandBuilder::new, builder);
  }
  
  public ExtraCommandBuilder<E, K, S> update(Consumer<UpdateCommandBuilder<E, K, S>> builder) {
    return builder(UpdateCommandBuilder::new, builder);
  }
  
  public ExtraCommandBuilder<E, K, S> runnable(Consumer<RunnableCommandBuilder<E, K, S>> builder) {
    return builder(RunnableCommandBuilder::new, builder);
  }
  
  public ExtraCommandBuilder<E, K, S> create(Consumer<CreateCommandBuilder<E, K, S>> builder) {
    return builder(CreateCommandBuilder::new, builder);
  }
  
  public ExtraCommandBuilder<E, K, S> list(Consumer<ListCommandBuilder<E, K, S>> builder) {
    return builder(ListCommandBuilder::new, builder);
  }
  
  public Command build(String commandName) {
    Command command = new Command(commandName);
    
    builders.forEach(builder -> builder.register(command));
    
    return command;
  }
}
