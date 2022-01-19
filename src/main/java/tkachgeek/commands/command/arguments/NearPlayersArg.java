package tkachgeek.commands.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;

import java.util.Comparator;
import java.util.List;

public class NearPlayersArg extends Argument {
  int radius;
  int limit;
  /**
   * При радиусе <=0 он не считается,<br>
   * при limit <= 0 он Integer.MAX_VALUE.<br>
   * Для всех игроков используйте {@link OnlinePlayers}, он быстрее
   */
  public NearPlayersArg(int radius, int limit) {
    this.radius = radius;
    this.limit = limit;
  }
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).anyMatch(x -> x.equalsIgnoreCase(raw));
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    if (sender instanceof Player completeFor) {
      return Bukkit.getOnlinePlayers().stream()
         .parallel()
         .filter(player -> radius <= 0 || completeFor.getLocation().distance(player.getLocation()) < radius && !player.equals(completeFor))
         .sorted(Comparator.comparingDouble(x -> x.getLocation().distance(completeFor.getLocation())))
         .limit(limit <= 0 ? Integer.MAX_VALUE : limit)
         .map(Player::getName)
         .toList();
    } else {
      return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).limit(limit <= 0 ? Integer.MAX_VALUE : limit).toList();
    }
  }
  
  @Override
  public String argumentName() {
    return "Ближайший игрок";
  }
}
