package ru.cwcode.commands.paperplatform.argument;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;

import java.util.ArrayList;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class OnlinePlayers extends Argument {
  public OnlinePlayers() {
  }
  
  @Override
  public boolean valid(String raw) {
    return Bukkit.getPlayer(raw) != null;
  }
  
  @Override
  public List<String> completions(Sender sender) {
    List<String> list = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      list.add(player.getName());
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
