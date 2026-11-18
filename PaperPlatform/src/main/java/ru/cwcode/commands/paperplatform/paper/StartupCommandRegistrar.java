package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Регистрация команды средствами сервера при старте (Brigadier на Paper 1.20.6+). Реализация
 * живёт в модуле PaperBrigadier (Java 21) и подключается в {@code PaperMain} только там, где есть
 * Lifecycle API, поэтому PaperPlatform знает её лишь через этот интерфейс.
 */
@FunctionalInterface
public interface StartupCommandRegistrar {
  /**
   * @return false, если стартовая регистрация уже прошла
   */
  boolean register(Plugin owner, Command command, String description, List<String> aliases, String permission,
                   Function<CommandSender, Sender> senders,
                   BiConsumer<CommandSender, String[]> executor, BiFunction<CommandSender, String[], List<String>> completer);
}
