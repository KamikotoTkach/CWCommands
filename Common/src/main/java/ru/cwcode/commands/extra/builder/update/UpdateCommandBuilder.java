package ru.cwcode.commands.extra.builder.update;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.commands.extra.TriConsumer;
import ru.cwcode.commands.extra.builder.CommandBuilder;
import ru.cwcode.commands.extra.command.executor.ExtraExecutor;

import java.util.HashMap;

public class UpdateCommandBuilder<E, K, S extends Sender> extends CommandBuilder<E, K, S> {
  HashMap<String, UpdatableField<E, Object, S>> fields = new HashMap<>();
  
  public UpdateCommandBuilder(RepositoryAccessor<E, K, S> builder) {
    super(builder);
  }
  
  public <A extends Argument> UpdateCommandBuilder<E, K, S> field(String name, A argument, TriConsumer<E, Object, S> consumer) {
    fields.put(name, new UpdatableField<>(name, argument, consumer));
    
    return this;
  }
  
  @Override
  public void register(Command command) {
    for (UpdatableField<E, Object, S> field : fields.values()) {
      command.arguments(
         new ArgumentSet(new ExtraExecutor<>(repositoryAccessor, (s, e, __) -> update(s, e, field), 1),
                         new ExactStringArg("update"),
                         repositoryAccessor.keyArgument(),
                         new ExactStringArg("set"),
                         new ExactStringArg(field.name),
                         field.argument
         ));
    }
  }
  
  private void update(S sender, E element, UpdatableField<E, Object, S> field) {
    field.consumer.accept(element, field.argument.map(), sender);
  }
}
