package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class NearPlayersArg extends Argument {
  int radius;
  int limit;
  
  /**
   * При радиусе меньше или равно 0 он не считается,<br>
   * при limit меньше или равно 0 он Integer.MAX_VALUE.<br>
   * Для всех игроков используйте {@link OnlinePlayers}, он быстрее
   */
  public NearPlayersArg(int radius, int limit) {
    this.radius = radius;
    this.limit = limit;
  }
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getOnlinePlayers().stream().map(Player::getName).anyMatch(x -> x.equalsIgnoreCase(raw));
  }
  
  @Override
  public List<String> completions(Sender sender) {
    List<Player> players = new ArrayList<>();
    
    if (sender instanceof Player) {
      Player completeFor = (Player) sender;
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (radius <= 0 || completeFor.getLocation()
                                      .distance(player.getLocation()) < radius && !player.equals(completeFor)) {
          players.add(player);
        }
      }
      
      players.sort(Comparator.comparingDouble(x -> x.getLocation().distance(completeFor.getLocation())));
    } else {
      players = new ArrayList<>(Bukkit.getOnlinePlayers());
    }
    
    List<String> list = new ArrayList<>();
    long limit1 = limit <= 0 ? Integer.MAX_VALUE : limit;
    
    for (Player player : players) {
      if (limit1-- == 0) break;
      list.add(player.getName());
    }
    
    return list;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.nearPlayer.name");
  }
  
  @Override
  public Object map() {
    return Bukkit.getPlayer(raw);
  }
}
