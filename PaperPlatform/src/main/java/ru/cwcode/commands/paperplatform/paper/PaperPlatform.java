package ru.cwcode.commands.paperplatform.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.PaperMain;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.logger.Logger;
import ru.cwcode.cwutils.logger.PaperLogger;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

import java.util.ArrayList;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

/**
 * Регистрация команды:
 * <ol>
 *   <li>при старте Paper 1.20.6+ — Brigadier через {@link StartupCommandRegistrar}, запись в plugin.yml не нужна;</li>
 *   <li>иначе — {@code CommandMap}: команда из plugin.yml получает наш исполнитель, без записи — регистрируется сама.</li>
 * </ol>
 * Команда из plugin.yml в любом случае получает исполнитель: на ней держатся алиасы из yml.
 */
public class PaperPlatform extends Platform {
  private final Logger logger = new PaperLogger(PaperMain.plugin);
  private StartupCommandRegistrar startupRegistrar;

  @Override
  public Logger getLogger() {
    return this.logger;
  }

  public void setStartupRegistrar(StartupCommandRegistrar startupRegistrar) {
    this.startupRegistrar = startupRegistrar;
  }

  @Override
  public void registerCommand(Command command) {
    PaperCommand handlers = new PaperCommand(command);
    PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getName());

    if (pluginCommand != null) {
      pluginCommand.setExecutor(handlers.getCommandParser());
      pluginCommand.setTabCompleter(handlers.getTabCompleter());
      if (pluginCommand.getDescription().isEmpty()) pluginCommand.setDescription(command.description());
      if (pluginCommand.getPermission() == null) pluginCommand.setPermission(command.getPermission());
    }

    Plugin owner = pluginCommand != null ? pluginCommand.getPlugin() : owner(command);
    String permission = pluginCommand != null ? pluginCommand.getPermission() : emptyToNull(command.getPermission());
    List<String> aliases = pluginCommand != null
                           ? new ArrayList<>(CollectionUtils.combine(pluginCommand.getAliases(), command.aliases()))
                           : command.aliases();

    if (startupRegistrar != null
        && startupRegistrar.register(owner, command.getName(), command.description(), aliases, permission,
                                     (sender, args) -> handlers.getCommandParser().onCommandExecute(new PaperSender(sender), command.getName(), args),
                                     (sender, args) -> handlers.getTabCompleter().onTabComplete(new PaperSender(sender), args))) {
      return;
    }

    if (pluginCommand == null) {
      Bukkit.getCommandMap().register(owner.getName(), new BukkitCommandAdapter(command.getName(), aliases, command.description(), permission, handlers));
      return;
    }

    List<String> extraAliases = new ArrayList<>(command.aliases());
    extraAliases.removeAll(pluginCommand.getAliases());
    if (!extraAliases.isEmpty()) {
      String label = extraAliases.remove(0);
      Bukkit.getCommandMap().register(owner.getName(), new BukkitCommandAdapter(label, extraAliases, command.description(), permission, handlers));
    }
  }

  /**
   * Плагин, чей класс исполняет команду; если исполнителей нет или класс не из плагина — CWCommands.
   */
  private static Plugin owner(Command command) {
    try {
      return command.anyExecutor()
                    .<Plugin>map(executor -> JavaPlugin.getProvidingPlugin(executor.getClass()))
                    .orElse(PaperMain.plugin);
    } catch (IllegalArgumentException | IllegalStateException notAPluginClass) {
      return PaperMain.plugin;
    }
  }

  private static String emptyToNull(String value) {
    return value == null || value.isEmpty() ? null : value;
  }

  @Override
  public void handleExecutionException(Exception exception, Command command, Sender sender) {
    if (exception instanceof MessageReturn) {
      MessageReturn messageReturn = (MessageReturn) exception;
      if (messageReturn.isStyled()) {
        sender.sendMessage(messageReturn.getComponentMessage());
      } else {
        sender.sendMessage(messageReturn.getComponentMessage()
                                        .color(command.getColorScheme().main()));
      }
      return;
    } else if (exception instanceof TargetableMessageReturn) {
      sender.sendMessage(((TargetableMessageReturn) exception).getMessage(((PaperSender) sender).getCommandSender()));
      return;
    }

    String message = userFacingMessage(exception);
    if (message == null) message = l10n.get("paperPlatform.unknownError");

    sender.sendMessage(Component.text(message, command.getColorScheme().main()));

    logExecutionError(l10n.get("paperPlatform.executionError", exception.getClass().getSimpleName()), command, exception);
  }
}
