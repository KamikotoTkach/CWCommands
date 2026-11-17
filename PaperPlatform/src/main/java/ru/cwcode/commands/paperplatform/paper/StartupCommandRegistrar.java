package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

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
  boolean register(Plugin owner, String label, String description, List<String> aliases, String permission,
                   BiConsumer<CommandSender, String[]> executor, BiFunction<CommandSender, String[], List<String>> completer);
}
