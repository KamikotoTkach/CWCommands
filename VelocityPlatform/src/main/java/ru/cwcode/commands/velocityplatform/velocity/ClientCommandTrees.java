package ru.cwcode.commands.velocityplatform.velocity;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import ru.cwcode.commands.BrigadierTree;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Полные деревья команд CWCommands для клиента. Velocity кладёт в дерево игрока копию нашего серверного
 * узла (метка + хвост); здесь он заменяется полным деревом {@link BrigadierTree#build} без веток, которые
 * игроку не видны. Корень, невидимый игроку, убирается целиком — команда пропадает из подсказок клиента,
 * но исполняется, как раньше: отказ сообщает наш парсер.
 * <p>
 * Узел трогается, только если Velocity его уже положил: при {@code announce-proxy-commands = false} дерево
 * не меняется. Событие приходит на каждый пакет команд от бэкенда (вход, смена сервера, пересылка дерева
 * после смены прав на бэкенде), поэтому права на прокси применяются с этим же пакетом.
 */
public class ClientCommandTrees {
  private final List<Entry> entries = new CopyOnWriteArrayList<>();

  /**
   * @param names метка и алиасы команды в нижнем регистре, как их зарегистрировал Velocity
   * @param tree  полное дерево; {@code requires} корня — видимость команды
   */
  public void add(List<String> names, LiteralCommandNode<CommandSource> tree) {
    entries.add(new Entry(List.copyOf(names), tree));
  }

  @Subscribe
  @SuppressWarnings("unchecked")
  public void onAvailableCommands(PlayerAvailableCommandsEvent event) {
    inject((RootCommandNode<CommandSource>) event.getRootNode(), event.getPlayer());
  }

  /**
   * Подменяет наши узлы в дереве {@code root}, которое уйдёт клиенту {@code source}.
   */
  public void inject(RootCommandNode<CommandSource> root, CommandSource source) {
    for (Entry entry : entries) {
      entry.injectInto(root, source);
    }
  }

  private record Entry(List<String> names, LiteralCommandNode<CommandSource> tree) {
    void injectInto(RootCommandNode<CommandSource> root, CommandSource source) {
      boolean visible = tree.canUse(source);
      for (String name : names) {
        if (root.getChild(name) == null) continue;
        root.removeChildByName(name);
        if (visible) root.addChild(BrigadierTree.visibleCopy(tree, name, source));
      }
    }
  }
}
