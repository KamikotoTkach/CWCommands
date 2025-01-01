# CWCommands 1.4.5

_(aka TkachCommands, PaperCommands, VelocityCommands)_\
_Velocity & Paper 1.16.5+_

Библиотека на команды.

Подключение:

* Paper:
  ```xml
  <dependency>
    <groupId>ru.cwcode.commands</groupId>
    <artifactId>PaperCommands</artifactId>
    <version>1.4.5</version>
    <scope>provided</scope>
  </dependency>
  ```
* Velocity:
  ```xml
  <dependency>
    <groupId>ru.cwcode.commands</groupId>
    <artifactId>VelocityCommands</artifactId>
    <version>1.4.5</version>
    <scope>provided</scope>
  </dependency>
  ```
* Common
  ```xml
  <dependency>
    <groupId>ru.cwcode.commands</groupId>
    <artifactId>Common</artifactId>
    <version>1.4.5</version>
    <scope>provided</scope>
  </dependency>
  ```

<br><br>

### Фиачурес:

- Авто-генерируемый хелп
- Авто-валидация аргуметов
- Авто таб-комплишен
- Готовые аргументы на все случаи жизни (37 шт.)
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
    ).register();
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

Ищет подходящий метод в зависимости от аргументов и мапит аргументы в объекты. Для правильной работы кастомных
аргументов им нужно переопределить метод Argument::map. Во всех стандартных аргументах он переопределён. Примитивные
типы заменять объектным аналогом (int -> Integer).

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

Предусловия проверяются перед непосредственным исполнением экзекутора и по их результату или выполняется экзекутор или
отправителю команды пишется что не так

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

### Встроенные аргументы

Формат: Название класса : тип, в который мапится в AutowiredExecutor

Модуль Common:

* Basic:
    - **IntegerArg** : int
    - **DoubleArg** : double
    - **BooleanArg** : boolean
    - **StringArg** : string
* Datetime:
    - **DurationArg** : Duration
    - **TimeArg** : string
* Spaced:
    - **SpacedStringArg** : string
    - **SpacedListArg** : string
    - **SpacedDynamicList** : string
    - **SpacedDynamicArg** : string
    - **SafetySpacedStringArg** : string
* Another:
    - **ComplexArg** (обёртка для нескольких аргументов в одном)
    - **DynamicList** : string
    - **EmptyArg** : string
    - **EnumArg** : enum instance
    - **ExactStringArg** : string
    - **HaxColorArg** : string
    - **LegacyColorArg** : string
    - **ListArg** : string
    - **SafetyStringArg** : string

Модуль Paper:

* Location:
    - **LocationArg** (комплексный аргумент из TargetXArg-ов xyz/xyz+world)
    - **TargetXArg**(LocationPart) : xyz->double, pitch yaw - > float, world -> org.bukkit.World
* Another:
    - **BlockArg** : enum instance
    - **EnchantmentArg** : org.bukkit.enchantments.Enchantment
    - **MaterialArg** : enum instance
    - **NearPlayersArg** : org.bukkit.Player
    - **OnlinePlayersArg** : org.bukkit.Player
    - **OninePlayersWithoutSender** : org.bukkit.Player
    - **OnlinePlayerWithPermissionArg** : org.bukkit.Player
    - **ParticleArg** : enum instance
    - **PlayerArg** : org.bukkit.Player
    - **PotionEffectArg** : org.bukkit.potion.PotionEffectType
    - **SoundArg** : enum instance
    - **WorldArg** : org.bukkit.World

Модуль Velocity:

- **OnlinePlayersArg** : proxy player
- **OninePlayersWithoutSender** : proxy player
- **OnlinePlayerWithPermissionArg** : proxy player
- **PlayerArg** : proxy player

Для сборки нужны TkachUtils
