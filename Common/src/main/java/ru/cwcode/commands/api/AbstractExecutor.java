package ru.cwcode.commands.api;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;
import tkachgeek.tkachutils.text.SpacesHider;

import java.util.Optional;

public abstract class AbstractExecutor {
  protected ArgumentParser parser;
  protected Sender sender;
  protected Command command = null;
  
  public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
    this.parser = new ArgumentParser(args, argumentSet);
    this.sender = sender;
    this.command = command;
    
    try {
      if (sender.isPlayer()) {
        executeForPlayer();
      } else {
        executeForNonPlayer();
      }
    } catch (Exception exception) {
      errorHandler(exception);
    }
  }
  
  /**
   * Действие, выполняемое для игроков И НЕ-ИГРОКОВ, если метод executeForNonPlayer не переопределён
   */
  public abstract void executeForPlayer() throws MessageReturn, TargetableMessageReturn;
  
  public void executeForNonPlayer() throws MessageReturn, TargetableMessageReturn {
    executeForPlayer();
  }
  
  /**
   * Переопределение обработчика ошибок
   */
  public abstract void errorHandler(Exception exception);
  
  /**
   * Возвращает игрока при выполнении команды игроком. Для не-игроков используй sender()
   */
  
  
  protected final Sender sender() {
    return sender;
  }
  
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
   * Возвращает строку с заменёнными подчёркиваниями на пробелы
   */
  protected String argWithSpaces(int index) {
    return SpacesHider.restore(arg(index).toString());
  }
  
  protected Command getCommand() {
    return command;
  }
}
