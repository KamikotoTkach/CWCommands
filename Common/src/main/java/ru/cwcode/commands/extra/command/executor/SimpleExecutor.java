package ru.cwcode.commands.extra.command.executor;

import net.kyori.adventure.audience.Audience;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.executor.AbstractExecutor;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.util.Optional;
import java.util.function.BiConsumer;

public class SimpleExecutor<S extends Sender> extends AbstractExecutor {
  BiConsumer<S, ArgumentParser> onExecute;
  
  public SimpleExecutor(BiConsumer<S, ArgumentParser> onExecute) {
    this.onExecute = onExecute;
  }
  
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    onExecute.accept((S) sender, parser);
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
