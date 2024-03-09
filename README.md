# TkachCommands 1.1.1
 _(aka CWCommands, PaperCommands, VelocityCommands)_\
_Velocity & Paper 1.16.5+_

Библиотека на команды. Возможно, когда-то сделаю нормальную документацию, но лень, и пока это останется только для личного использования.
<br><br>
### Фиачурес:
- Авто-генерируемый хелп
- Авто-валидация аргуметов
- Авто таб-комплишен
- Готовые аргументы на все случаи жизни (34 шт.)
- Кастомизация цветов
- Авто-генерация пермишенов (переопределяемая)
- Опциональные аргументы
- Spaced-аргументы (implements SpacedArgument)
- Динамические аргументы
- AutowiredExecutor
- Preconditions (предусловия)

### Использование:
```java
    new Command("rootCommandName", "rootPermission")
       .subCommands(
         //new Command...
       )
       .arguments(
         new ArgumentSet(new ExecutorExample(),
                         new ExactStringArg("giveItem"),
                         new DynamicList("name", ItemRepo::getIDs),
                         new PlayerArg()
                            .optional(),
                         new IntegerArg()
                            .setMin(1)
                            .optional()).help("help текст")
    //, new ArgumentSet...
    ).register(this); //this = JavaPlugin
```

Пример Executor`a:
```java
public class ExecutorExample extends Executor {
  @Override
  public void executeForPlayer() {
    Player player = player();
    int amount = 1;
    
    if (isPresent(2)) player = Bukkit.getPlayer(argS(2));
    
    if (player == null) {
      sender.sendMessage("No player with name " + argS(2));
      return;
    }
    
    if (isPresent(3)) amount = argI(3);
    
    player.getInventory().addItem(ItemRepo.getItem(argS(1)).asQuantity(amount));
  }
```
Аргументы можно именовать и получать по их имени в экзекуторе
```java
new IntegerArg().tag("amount");
//---
arg("amount).toInt()
```

Пример авто-хелпа:
<br><br>
![image](https://github.com/KamikotoTkach/TkachCommands/assets/110531613/bc1b3be2-f4f2-44a5-8677-cdee313e8a6d)
<br><br>
Что примерно соответствует этому коду: 
<br><br>
![image](https://github.com/KamikotoTkach/TkachCommands/assets/110531613/1fc3f972-0b54-4473-88ae-ac5bd84cbc12)
<br><br>

### AutowiredExecutor
Ищет подходящий метод в зависимости от аргументов и мапит аргументы в объекты. Для правильной работы кастомных аргументов им нужно переопределить метод Argument::map. Во всех стандартных аргументах он переопределён.

```java
new ArgumentSet(new TestAutowired(), new ExactStringArg("testAutowired"), new SomeObjectArg().optional())

//............

public class TestAutowired extends AutowiredExecutor {

//В зависимости от того, предоставлен ли опциональный аргумент, будет выбран подходящий метод:

  public void test(SomeObject object) {
    sender.sendMessage(object.toString());
  }
  
  public void test() {
    sender.sendMessage("optional object not present");
  }
}

```

### Preconditions
Предусловия проверяются перед непосредственным исполнением экзекутора и по их результату или выполняется экзекутор или отправителю команды пишется что не так

```java
new Command("command")
       .preconditions(new LoadedPlayerData(), new SomethingElse())
//......

public class LoadedPlayerData extends Precondition {
  @Override
  public boolean canExecute(Sender sender) {
    return sender.isPlayer()
       && sender instanceof PaperSender paperSender
       && playerDataManager.getPlayerData(paperSender.getPlayer()).isPresent();
  }
  
  @Override
  public String cannotExecuteFeedback(Sender sender) {
    return "Подождите немного или перезайдите на сервер: ваши даные не удалось загрузить";
  }

  //Если в предусловии переопределить этот метод, то команда/аргументсет, если не доступен игроку, будет исключён из списка досупных для игрока в принципе
  @Override
  public boolean canSee(Sender sender) {
    return true;
  }
}

```




Для сборки нужны TkachUtils
