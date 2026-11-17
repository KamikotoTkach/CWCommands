package ru.cwcode.commands.paperplatform.brigadier;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Регистрирует команды в Brigadier через {@code LifecycleEvents.COMMANDS} (Paper 1.20.6+) от имени
 * плагина-владельца: корневой literal и одна greedy-строка, разбор и подсказки — наши.
 * Обработчик ставится один раз из {@code onEnable} CWCommands; команды, пришедшие после
 * стартового события, сюда уже не попадают ({@link #add} возвращает false).
 */
@SuppressWarnings("UnstableApiUsage")
public class LifecycleCommandRegistrar {
  private final List<Registration> registrations = new ArrayList<>();
  private boolean startupPassed = false;

  public LifecycleCommandRegistrar(JavaPlugin plugin) {
    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      // /minecraft:reload пересоздаёт диспетчер и вызывает событие снова — список не очищается
      for (Registration registration : registrations) {
        registration.registerInto(event.registrar());
      }
      startupPassed = true;
    });
  }

  /**
   * @return false, если стартовая регистрация уже прошла и команду нужно регистрировать иначе
   */
  public boolean add(Plugin owner, String label, @Nullable String description, List<String> aliases, @Nullable String permission,
                     BiConsumer<CommandSender, String[]> executor, BiFunction<CommandSender, String[], List<String>> completer) {
    if (startupPassed) return false;

    registrations.add(new Registration(owner.getPluginMeta(), label, description, List.copyOf(aliases),
                                       new Handler(permission, executor, completer)));
    return true;
  }

  private record Registration(PluginMeta owner, String label, @Nullable String description, List<String> aliases, BasicCommand command) {
    void registerInto(Commands commands) {
      commands.register(owner, label, description, aliases, command);
    }
  }

  private record Handler(@Nullable String permission,
                         BiConsumer<CommandSender, String[]> executor,
                         BiFunction<CommandSender, String[], List<String>> completer) implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
      executor.accept(source.getSender(), args);
    }

    /**
     * На «/cmd » Paper передаёт пустой массив, а наш {@code TabCompleter} ждёт [""] — как Bukkit.
     */
    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
      List<String> suggestions = completer.apply(source.getSender(), args.length == 0 ? new String[]{""} : args);
      return suggestions == null ? List.of() : new ArrayList<>(suggestions);
    }
  }
}
