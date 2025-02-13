package ru.cwcode.commands.paperplatform.executor;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class PlayerExecute extends Executor {
  final Consumer<Player> onExecute;
  
  public PlayerExecute(Consumer<Player> onExecute) {
    this.onExecute = onExecute;
  }
  
  @Override
  public void executeForPlayer() {
    onExecute.accept(player());
  }
  
  @Override
  public void executeForNonPlayer() {
    sender.sendMessage(l10n.get("executor.onlyForPlayers"));
  }
}
