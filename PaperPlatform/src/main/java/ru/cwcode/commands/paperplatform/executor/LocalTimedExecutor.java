package ru.cwcode.commands.paperplatform.executor;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.executor.AbstractLocalTimedExecutor;
import ru.cwcode.commands.paperplatform.paper.PaperSender;

import java.util.Optional;

public abstract class LocalTimedExecutor extends AbstractLocalTimedExecutor {
  
  public LocalTimedExecutor(long delayMillis) {
    super(delayMillis);
  }
  
  @Override
  public void handleError(Exception exception) {
    CommandsAPI.getPlatform().handleExecutionException(exception, command, sender);
  }
  
  @Override
  protected Audience sender() {
    return sender.getAudience();
  }
  
  @Override
  protected Optional<Player> argP(int index) {
    Player player = Bukkit.getPlayer(argS(index));
    if (player == null) return Optional.empty();
    
    return Optional.of(player);
  }
  
  protected final Player player() {
    if (sender instanceof PaperSender) {
      return ((PaperSender) sender).getPlayer();
    }
    
    return null;
  }
  
  protected final Material argM(int index) {
    return Material.matchMaterial(argS(index));
  }
}
