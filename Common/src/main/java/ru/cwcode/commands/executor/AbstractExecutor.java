package ru.cwcode.commands.executor;

import net.kyori.adventure.audience.Audience;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.cwutils.collections.EnumUtils;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;
import ru.cwcode.cwutils.text.SpacesHider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Экземпляр из описания команды — шаблон: каждый вызов исполняется на его поверхностной копии
 * со своими {@code parser}, {@code sender} и {@code command}. Поэтому один исполнитель безопасно
 * обслуживает параллельные (Velocity) и вложенные вызовы, а {@code player()}/{@code sender()}
 * остаются рабочими в async-продолжениях, начатых из метода команды.
 */
public abstract class AbstractExecutor implements Cloneable {
  protected ArgumentParser parser;
  protected Sender sender;
  protected Command command = null;

  public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
    AbstractExecutor invocation = copyForInvocation();
    invocation.parser = new ArgumentParser(args, argumentSet);
    invocation.sender = sender;
    invocation.command = command;
    invocation.run();
  }

  private void run() {
    try {
      if (sender.isPlayer()) {
        executeForPlayer();
      } else {
        executeForNonPlayer();
      }
    } catch (Exception exception) {
      handleError(exception);
    }
  }

  private AbstractExecutor copyForInvocation() {
    try {
      return (AbstractExecutor) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  /**
   * Может ли исполнитель принять набор из {@code count} значений. Проверяется при регистрации команды.
   */
  public boolean acceptsArgumentCount(int count) {
    return true;
  }

  /**
   * Ошибки описания исполнителя, найденные при его создании; выводятся при регистрации команды.
   */
  public List<String> definitionProblems() {
    return Collections.emptyList();
  }

  /**
   * Действие, выполняемое для игроков И НЕ-ИГРОКОВ, если метод executeForNonPlayer не переопределён
   */
  public abstract void executeForPlayer();
  
  public void executeForNonPlayer() {
    executeForPlayer();
  }
  
  /**
   * Переопределение обработчика ошибок
   */
  public abstract void handleError(Exception exception);
  
  /**
   * Возвращает игрока при выполнении команды игроком. Для не-игроков используй sender()
   */
  
  
  protected abstract Audience sender();
  
  /**
   * Получает аргумент по тегу, если его нет - null. Дефолтный тэг - результат выполнения метода argumentName у аргумента
   */
  public Argument arg(String tag) {
    return parser.get(tag);
  }
  
  /**
   * Получает аргумент опционально
   */
  protected final Optional<Argument> argO(int index) {
    if (isPresent(index)) {
      return Optional.of(arg(index));
    } else {
      return Optional.empty();
    }
  }
  
  /**
   * Получает аргумент по индексу, если его нет - null
   */
  protected final Argument arg(int index) {
    return parser.get(index);
  }
  
  /**
   * Проверяет есть ли аргумент под таким индексом
   */
  public boolean isPresent(int index) {
    return argumentsAmount() > index;
  }
  
  protected final int argumentsAmount() {
    return parser.args.length;
  }
  
  /**
   * Получает аргумент и кастует его в double, если его нет - эксепшен
   */
  protected double argD(int index) {
    return arg(index).toDouble();
  }
  
  /**
   * Получает аргумент и кастует его в int, если его нет - эксепшен
   */
  protected int argI(int index) {
    return arg(index).toInt();
  }
  
  /**
   * Получает аргумент и кастует его в строку, если его нет - эксепшен
   */
  protected String argS(int index) {
    return arg(index).toString();
  }
  
  /**
   * Получает аргумент и кастует его в boolean, если его нет - эксепшен
   */
  protected boolean argB(int index) {
    return arg(index).toBoolean();
  }
  
  /**
   * Получает аргумент и получает из него enum (регистронезависимо)
   */
  protected <T extends Enum<?>> Optional<T> argE(int index, Class<? extends T> enumClass) {
    try {
      return EnumUtils.getEnumInstance(EnumUtils.getEnumValues(enumClass), argS(index));
    } catch (IllegalAccessException | NoSuchFieldException e) {
      return Optional.empty();
    }
  }
  
  protected abstract Optional<? extends Audience> argP(int index);
  
  /**
   * Возвращает строку с заменёнными подчёркиваниями на пробелы
   */
  protected String argWithSpaces(int index) {
    return SpacesHider.restore(arg(index).toString());
  }
  
  protected Command getCommand() {
    return command;
  }
}
