package ru.cwcode.commands.velocityplatform.velocity;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import ru.cwcode.commands.BrigadierTree;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Platform;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.cwutils.logger.Logger;
import ru.cwcode.cwutils.logger.VelocityLogger;
import ru.cwcode.cwutils.messages.MessageReturn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

/**
 * Команда на прокси — {@link BrigadierCommand} из двух узлов: метка и «хвост» на всю строку
 * ({@link BrigadierTree#buildCatchAll}). Разбор, исполнение и подсказки — наши, как и раньше с
 * {@code RawCommand}; Velocity исполняет команду в пуле потоков CWCommands, то есть параллельно.
 * <p>
 * Полное дерево с подкомандами и типами аргументов получает только клиент: {@link ClientCommandTrees}
 * подменяет им наш узел в {@code PlayerAvailableCommandsEvent}, отрезав ветки, скрытые от этого игрока.
 * Серверное дерево полного не содержит, поэтому скрытые подкоманды не попадают в подсказки, а
 * {@code requires} не мешают разбору.
 */
public class VelocityPlatform extends Platform {
  private final VelocityMain plugin;
  private final ProxyServer server;
  private final Logger logger;
  private final org.slf4j.Logger slf4j;
  private final List<Command> registered = new CopyOnWriteArrayList<>();
  private final ClientCommandTrees clientTrees = new ClientCommandTrees();

  public VelocityPlatform(VelocityMain plugin, ProxyServer server, org.slf4j.Logger logger) {
    this.plugin = plugin;
    this.server = server;
    this.slf4j = logger;
    this.logger = new VelocityLogger(logger);
  }

  public VelocityMain getPlugin() {
    return plugin;
  }

  public ProxyServer getServer() {
    return server;
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  /**
   * Слушатель {@code PlayerAvailableCommandsEvent}; {@link VelocityMain} регистрирует его при старте.
   */
  public ClientCommandTrees getClientTrees() {
    return clientTrees;
  }

  /**
   * Все корневые команды, прошедшие {@link #registerCommand}, — для проверок вроде {@code /commandstestv brigadier}.
   */
  public List<Command> registeredCommands() {
    return Collections.unmodifiableList(registered);
  }

  @Override
  public void registerCommand(Command command) {
    try {
      VelocityCommand handlers = new VelocityCommand(command);
      String label = command.getName().toLowerCase(Locale.ROOT);
      List<String> aliases = aliases(command, label);

      BrigadierTree<CommandSource> tree = new BrigadierTree<>(
        VelocitySender::new,
        (source, args) -> handlers.getCommandParser().onCommandExecute(new VelocitySender(source), command.getName(), args),
        (source, args) -> handlers.getTabCompleter().onTabComplete(new VelocitySender(source), args),
        source -> true);

      CommandManager commandManager = server.getCommandManager();
      commandManager.register(commandManager.metaBuilder(label)
                                            .aliases(aliases.toArray(new String[0]))
                                            .plugin(plugin)
                                            .build(),
                              new BrigadierCommand(tree.buildCatchAll(label)));

      LiteralCommandNode<CommandSource> clientTree = tree.build(command, label, tree.rootVisibility(command));
      List<String> names = new ArrayList<>();
      names.add(label);
      names.addAll(aliases);
      clientTrees.add(names, clientTree);

      registered.add(command);
    } catch (Exception e) {
      slf4j.warn(l10n.get("velocityPlatform.commandIsNotRegistered", command.getName()), e);
    }
  }

  /**
   * Velocity сравнивает метки в нижнем регистре: алиас с заглавной буквой иначе не нашёлся бы.
   */
  private static List<String> aliases(Command command, String label) {
    List<String> aliases = new ArrayList<>();
    for (String alias : command.aliases()) {
      String lower = alias.toLowerCase(Locale.ROOT);
      if (!lower.equals(label) && !aliases.contains(lower) && lower.indexOf(' ') < 0) aliases.add(lower);
    }
    return aliases;
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
    }

    String message = userFacingMessage(exception);
    if (message == null) message = l10n.get("velocityPlatform.unknownError");

    sender.sendMessage(Component.text(message, command.getColorScheme().main()));

    logExecutionError(l10n.get("velocityPlatform.executionError", exception.getClass().getSimpleName()), command, exception);
  }
}
