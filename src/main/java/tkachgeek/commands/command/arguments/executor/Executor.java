package tkachgeek.commands.command.arguments.executor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.ArgumentParser;
import tkachgeek.commands.command.ArgumentSet;

public abstract class Executor {
  ArgumentParser parser;
  CommandSender sender;
  
  /**
   * Получает аргумент по индексу, если его нет - null
   */
  protected final Argument arg(int index) {
    return parser.get(index);
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
  
  protected final int argumentsAmount() {
    return parser.args.length;
  }
  
  /**
   * Возвращает игрока при выполнении команды игроком. Для не-игроков используй sender()
   */
  protected final Player player() {
    return (Player) sender;
  }
  
  protected final CommandSender sender() {
    return sender;
  }
  
  public void prepare(CommandSender sender, String[] args, ArgumentSet argumentSet) {
    parser = new ArgumentParser(args, argumentSet);
    this.sender = sender;
    
    try {
      if (sender instanceof Player) {
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
  public abstract void executeForPlayer() throws MessageReturn;
  
  public void executeForNonPlayer() throws MessageReturn {
    executeForPlayer();
  }
  
  /**
   * Переопределение обработчика ошибок
   */
  public void errorHandler(Exception exception) {
    if (exception instanceof MessageReturn) {
      sender.sendMessage(((MessageReturn) exception).getComponentMessage());
      return;
    }
    
    sender.sendMessage(exception.getLocalizedMessage());
    
    Bukkit.getLogger().warning("Ошибка при исполнении " + this.getClass().getName());
    exception.printStackTrace();
  }
  
  /**
   * Проверяет есть ли аргумент под таким индексом
   */
  public boolean isPresent(int index) {
    return argumentsAmount() > index;
  }
}
