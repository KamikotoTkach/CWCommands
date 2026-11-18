package ru.cwcode.commands.paperplatform.brigadier;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.cwcode.commands.BrigadierTree;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Регистрирует команды в Brigadier через {@code LifecycleEvents.COMMANDS} (Paper 1.20.6+) от имени
 * плагина-владельца полным деревом {@link BrigadierTree}: клиент подсвечивает подкоманды и аргументы,
 * разбор, исполнение и подсказки — наши. Обработчик ставится один раз из {@code onEnable} CWCommands;
 * команды, пришедшие после стартового события, сюда уже не попадают ({@link #add} возвращает false).
 * <p>
 * «Дерево для клиента» = {@code requires} спрашивают не с главного потока: Paper строит его в своём
 * пуле {@code Paper Async Command Builder}, а разбор команд и серверных подсказок идёт на главном.
 */
@SuppressWarnings("UnstableApiUsage")
public class LifecycleCommandRegistrar implements Listener {
  private final List<Registration> registrations = new ArrayList<>();
  private final Map<String, BiFunction<CommandSender, String[], List<String>>> completers = new ConcurrentHashMap<>();
  private boolean startupPassed = false;

  public LifecycleCommandRegistrar(JavaPlugin plugin) {
    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      // /minecraft:reload пересоздаёт диспетчер и вызывает событие снова — список не очищается
      for (Registration registration : registrations) {
        registration.registerInto(event.registrar());
      }
      startupPassed = true;
    });
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  /**
   * @return false, если стартовая регистрация уже прошла и команду нужно регистрировать иначе
   */
  public boolean add(Plugin owner, Command command, @Nullable String description, List<String> aliases, @Nullable String permission,
                     Function<CommandSender, Sender> senders,
                     BiConsumer<CommandSender, String[]> executor, BiFunction<CommandSender, String[], List<String>> completer) {
    if (startupPassed) return false;

    BrigadierTree<CommandSourceStack> tree = new BrigadierTree<>(source -> senders.apply(source.getSender()),
                                                                 (source, args) -> executor.accept(source.getSender(), args),
                                                                 (source, args) -> completer.apply(source.getSender(), args),
                                                                 source -> !Bukkit.isPrimaryThread());
    registrations.add(new Registration(owner.getPluginMeta(), command, description, List.copyOf(aliases), permission, tree));

    completers.put(command.getName().toLowerCase(), completer);
    for (String alias : aliases) {
      completers.put(alias.toLowerCase(), completer);
    }
    return true;
  }

  /**
   * Brigadier собирает серверные подсказки со всех детей узла, не глядя на {@code requires}, — вместе со
   * скрытыми подкомандами. Оставляем только то, что дал наш {@code TabCompleter}, как было до дерева.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onSuggestions(AsyncPlayerSendSuggestionsEvent event) {
    String buffer = event.getBuffer();
    String input = buffer.startsWith("/") ? buffer.substring(1) : buffer;
    int labelEnd = input.indexOf(' ');
    if (labelEnd < 0) return;

    String label = input.substring(0, labelEnd).toLowerCase();
    label = label.substring(label.indexOf(':') + 1);

    BiFunction<CommandSender, String[], List<String>> completer = completers.get(label);
    if (completer == null) return;

    List<String> ours = completer.apply(event.getPlayer(), input.substring(labelEnd + 1).split(" ", -1));
    event.setSuggestions(BrigadierTree.keepOurs(buffer, event.getSuggestions(), ours == null ? List.of() : ours));
  }

  private record Registration(PluginMeta owner, Command command, @Nullable String description, List<String> aliases,
                              @Nullable String permission, BrigadierTree<CommandSourceStack> tree) {
    void registerInto(Commands commands) {
      commands.register(owner, tree.build(command, command.getName(), this::permitted), description, aliases);
    }

    private boolean permitted(CommandSourceStack source) {
      return permission == null || source.getSender().hasPermission(permission);
    }
  }
}
