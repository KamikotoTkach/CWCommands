package ru.cwcode.commands.extra.command.executor;

import net.kyori.adventure.audience.Audience;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.commands.extra.TriConsumer;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.util.Optional;

public class ExtraExecutor<E, K, S extends Sender> extends AbstractExecutor {
  private final TriConsumer<S, E, ArgumentParser> onExecute;
  private final int keyIndex;
  private final RepositoryAccessor<E, K, S> builder;
  
  public ExtraExecutor(RepositoryAccessor<E, K, S> builder, TriConsumer<S, E, ArgumentParser> onExecute, int keyIndex) {
    this.onExecute = onExecute;
    this.keyIndex = keyIndex;
    this.builder = builder;
  }
  
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    S senderCast = (S) sender;
    String stringKey = arg(keyIndex).toString();
    
    K key = builder.stringToKey(stringKey);
    E element = builder.keyToElement(senderCast, key);
    
    onExecute.accept(senderCast, element, parser);
  }
  
  @Override
  public void handleError(Exception exception) {
    exception.printStackTrace();
  }
  
  @Override
  protected Audience sender() {
    return null;
  }
  
  @Override
  protected Optional<? extends Audience> argP(int index) {
    return Optional.empty();
  }
}
