package ru.cwcode.commands.executor;

import revxrsal.asm.BoundMethodCaller;
import revxrsal.asm.MethodCaller;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.messages.MessageReturn;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public abstract class AbstractAutowiredExecutor extends AbstractExecutor {
  private final MethodTypeNode rootNode = new MethodTypeNode();
  
  public AbstractAutowiredExecutor() {
    for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
      if (declaredMethod.isSynthetic()) return;
      rootNode.register(declaredMethod, this);
    }
  }
  
  @Override
  public void executeForPlayer() {
    Argument[] args = parser.args;
    int argsLength = args.length;
    
    Object[] mapped = new Object[argsLength];
    Class<?>[] mappedTypes = new Class<?>[argsLength];
    int mappedParametersCount = 0;
    
    for (Argument arg : args) {
      if (arg instanceof ExactStringArg) continue;
      
      Object map = arg.map();
      mapped[mappedParametersCount] = map;
      mappedTypes[mappedParametersCount] = map.getClass();
      mappedParametersCount++;
    }
    
    
    BoundMethodCaller foundedMethod = rootNode.find(mappedTypes, mappedParametersCount);
    if (foundedMethod == null) {
      throw new MessageReturn(l10n.get("error.noSuchMethod", CollectionUtils.toString(Arrays.stream(mappedTypes)
                                                                                            .map(Class::getSimpleName)
                                                                                            .collect(Collectors.toList()))));
    }
    
    try {
      foundedMethod.call(Arrays.copyOf(mapped, mappedParametersCount));
    } catch (Exception e) {
      Throwable cause = e.getCause();
      
      if (cause instanceof Exception) {
        handleError((Exception) cause);
        return;
      }
      
      handleError(e);
    }
  }
  
  private static class MethodTypeNode {
    private BoundMethodCaller method;
    private final Map<Class<?>, MethodTypeNode> children = new HashMap<>();
    
    public void register(Method method, Object bindTo) {
      register(method.getParameterTypes(), MethodCaller.wrap(method).bindTo(bindTo), 0);
    }
    
    private void register(Class<?>[] parameterTypes, BoundMethodCaller method, int level) {
      if (level == parameterTypes.length) {
        this.method = method;
        return;
      }
      
      children.computeIfAbsent(parameterTypes[level], __ -> new MethodTypeNode())
              .register(parameterTypes, method, level + 1);
    }
    
    public BoundMethodCaller find(Class<?>[] parameterTypes, int depth) {
      return find(parameterTypes, 0, depth);
    }
    
    private BoundMethodCaller find(Class<?>[] parameterTypes, int level, int depth) {
      if (level == depth) {
        return method;
      }
      
      for (Map.Entry<Class<?>, MethodTypeNode> entry : children.entrySet()) {
        if (entry.getKey().isAssignableFrom(parameterTypes[level])) {
          BoundMethodCaller result = entry.getValue().find(parameterTypes, level + 1, depth);
          if (result != null) {
            return result;
          }
        }
      }
      
      return null;
    }
  }
}
