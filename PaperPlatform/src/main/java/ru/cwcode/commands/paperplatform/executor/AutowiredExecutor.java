package ru.cwcode.commands.paperplatform.executor;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.executor.AbstractAutowiredExecutor;
import ru.cwcode.commands.paperplatform.paper.PaperSender;

import java.util.Optional;

public class AutowiredExecutor extends AbstractAutowiredExecutor {
  @Override
  public void handleError(Exception exception) {
    CommandsAPI.getPlatform().handleExecutionException(exception, command, sender);
  }
  
  @Override
  public Audience sender() {
    return sender.getAudience();
  }
  
  @Override
  public Optional<Player> argP(int index) {
    Player player = Bukkit.getPlayer(argS(index));
    if (player == null) return Optional.empty();
    
    return Optional.of(player);
  }
  
  public final Player player() {
    if (sender instanceof PaperSender) {
      return ((PaperSender) sender).getPlayer();
    }
    
    return null;
  }
  
  public final Material argM(int index) {
    return Material.matchMaterial(argS(index));
  }
}
