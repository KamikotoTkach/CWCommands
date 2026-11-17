package ru.cwcode.commands.paperplatform.paper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Команда для {@code CommandMap} без записи в plugin.yml: старые серверы и регистрация после старта.
 */
class BukkitCommandAdapter extends org.bukkit.command.Command {
  private final PaperCommand handlers;

  BukkitCommandAdapter(String label, List<String> aliases, String description, String permission, PaperCommand handlers) {
    super(label, description == null ? "" : description, "/" + label, aliases);
    this.handlers = handlers;
    if (permission != null) setPermission(permission);
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
    // PluginCommand проверяет право сам, CommandMap — нет
    if (!testPermission(sender)) return true;
    return handlers.getCommandParser().onCommand(sender, this, label, args);
  }

  @Override
  public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    List<String> completions = handlers.getTabCompleter().onTabComplete(sender, this, alias, args);
    return completions == null ? Collections.emptyList() : completions;
  }
}
