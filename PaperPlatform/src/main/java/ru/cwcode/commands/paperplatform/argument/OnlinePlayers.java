package ru.cwcode.commands.paperplatform.argument;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.paper.PaperSender;

import java.util.ArrayList;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class OnlinePlayers extends Argument {
  public OnlinePlayers() {
  }
  
  @Override
  public boolean valid(String raw) {
    return false;
  }
  
  @Override
  public boolean valid(Sender sender, String raw, List<String> arguments) {
    PaperSender s = (PaperSender) sender;
    boolean isPlayer = s.isPlayer();
    boolean isBypass = !isPlayer || s.getPlayer().hasPermission("cwcommands.arg.onlineplayers.bypass");
    
    Player mathingPlayer = Bukkit.getPlayerExact(raw);
    if(mathingPlayer == null) return false;
    
    if(!isPlayer || isBypass) return true;
    
    return ((PaperSender) sender).getPlayer().canSee(mathingPlayer);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    PaperSender s = (PaperSender) sender;
    boolean isPlayer = s.isPlayer();
    boolean isBypass = !isPlayer || s.getPlayer().hasPermission("cwcommands.arg.onlineplayers.bypass");
    
    List<String> list = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (isBypass || s.getPlayer().canSee(player)) list.add(player.getName());
    }
    
    return list;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.onlinePlayer.name");
  }
  
  @Override
  public Component invalidMessage(Command command, Sender sender, String written) {
    return Component.text(l10n.get("argument.onlinePlayer.invalid", written), command.getColorScheme().main());
  }
  
  @Override
  public Object map() {
    return Bukkit.getPlayer(raw);
  }
}
