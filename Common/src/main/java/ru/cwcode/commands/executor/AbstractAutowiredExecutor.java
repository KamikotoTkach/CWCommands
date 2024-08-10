package ru.cwcode.commands.executor;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public abstract class AbstractAutowiredExecutor extends AbstractExecutor {
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    List<Object> objects = Arrays.stream(parser.args)
                                 .filter(arg -> arg.getClass() != ExactStringArg.class)
                                 .map(Argument::map)
                                 .collect(Collectors.toList());
    
    Method matchingMethod = Arrays.stream(getClass().getDeclaredMethods()).filter(method -> {
      if (objects.size() != method.getParameterCount()) return false;
      
      Class<?>[] parameterTypes = method.getParameterTypes();
      
      for (int i = 0; i < parameterTypes.length; i++) {
        if (!parameterTypes[i].isInstance(objects.get(i))) return false;
      }
      
      return true;
    }).findFirst().orElseThrow(() -> new MessageReturn(l10n.get("error.noSuchMethod", CollectionUtils.toString(objects.stream()
                                                                                                                      .map(x -> x.getClass().getSimpleName())
                                                                                                                      .collect(Collectors.toList())))));
    
    try {
      matchingMethod.invoke(this, objects.toArray());
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
