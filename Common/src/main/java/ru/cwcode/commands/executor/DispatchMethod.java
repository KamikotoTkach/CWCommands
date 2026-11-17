package ru.cwcode.commands.executor;

import revxrsal.asm.MethodCaller;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * Метод исполнителя как цель диспетчеризации: типы значений аргументов, по которым он ищется,
 * и как из этих значений собрать вызов (подставить отправителя, обернуть {@link Optional}).
 */
final class DispatchMethod {
  final Method method;
  final MethodCaller caller;
  /**
   * Тип первого параметра, если он получает отправителя, иначе null.
   */
  final Class<?> senderType;
  final AbstractAutowiredExecutor.SenderParameter senderParameter;
  /**
   * Типы значений аргументов (для {@code Optional<T>} — T), без параметра отправителя.
   */
  final Class<?>[] valueTypes;
  private final boolean[] optional;

  private DispatchMethod(Method method, MethodCaller caller, Class<?> senderType,
                         AbstractAutowiredExecutor.SenderParameter senderParameter) {
    this.method = method;
    this.caller = caller;
    this.senderType = senderType;
    this.senderParameter = senderParameter;

    int offset = senderType == null ? 0 : 1;
    Class<?>[] parameterTypes = method.getParameterTypes();
    Type[] genericTypes = method.getGenericParameterTypes();

    this.valueTypes = new Class<?>[parameterTypes.length - offset];
    this.optional = new boolean[valueTypes.length];

    for (int i = 0; i < valueTypes.length; i++) {
      Class<?> type = parameterTypes[i + offset];
      optional[i] = type == Optional.class;
      valueTypes[i] = optional[i] ? optionalElementType(genericTypes[i + offset]) : type;
    }
  }

  static DispatchMethod explicit(Method method, MethodCaller caller) {
    return new DispatchMethod(method, caller, null, AbstractAutowiredExecutor.SenderParameter.NONE);
  }

  static DispatchMethod withSender(Method method, MethodCaller caller, AbstractAutowiredExecutor.SenderParameter parameter) {
    return new DispatchMethod(method, caller, method.getParameterTypes()[0], parameter);
  }

  Object[] arguments(Object sender, Object[] values, int count) {
    int offset = senderType == null ? 0 : 1;
    Object[] result = new Object[valueTypes.length + offset];
    if (offset == 1) result[0] = sender;

    for (int i = 0; i < valueTypes.length; i++) {
      Object value = i < count ? values[i] : null;
      result[i + offset] = optional[i] ? Optional.ofNullable(value) : value;
    }
    return result;
  }

  String parameterNames() {
    return Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).reduce((a, b) -> a + ", " + b).orElse("");
  }

  private static Class<?> optionalElementType(Type type) {
    if (!(type instanceof ParameterizedType)) return Object.class;

    Type element = ((ParameterizedType) type).getActualTypeArguments()[0];
    if (element instanceof Class) return (Class<?>) element;
    if (element instanceof ParameterizedType) return (Class<?>) ((ParameterizedType) element).getRawType();
    return Object.class;
  }
}
