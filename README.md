Библиотека на команды. Возможно, когда-то сделаю нормальную документацию, но лень, и пока это останется только для личного использования.
<br><br>
Фиачурес:
- Авто-генерируемый хелп
- Авто-валидация аргуметов
- Авто таб-комплишен
- Готовые аргументы на все случаи жизни (34 шт.)
- Кастомизация цветов
- Авто-генерация пермишенов (переопределяемая)
- Опциональные аргументы
- Spaced-аргументы (implements SpacedArgument)
- Динамические аргументы

Использование:
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
Для сборки нужны TkachUtils
