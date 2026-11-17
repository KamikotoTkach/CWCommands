package ru.cwcode.commands.paperplatform.executor;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
  protected SenderParameter senderParameter(Class<?> type) {
    if (type == Player.class) return SenderParameter.PLAYER;
    if (type == CommandSender.class) return SenderParameter.ANY;
    return super.senderParameter(type);
  }

  @Override
  protected Object senderValue(Class<?> type) {
    if (type == Player.class) return player();
    if (type == CommandSender.class) return ((PaperSender) sender).getCommandSender();
    return super.senderValue(type);
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
