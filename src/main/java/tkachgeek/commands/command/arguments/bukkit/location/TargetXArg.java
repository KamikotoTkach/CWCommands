package tkachgeek.commands.command.arguments.bukkit.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.Argument;
import tkachgeek.tkachutils.numbers.NumbersUtils;

import java.util.Collections;
import java.util.List;

public class TargetXArg extends Argument {
  private final LocationPart part;
  
  public TargetXArg(LocationPart part) {
    this.part = part;
  }
  
  @Override
  public boolean valid(String raw) {
    switch (part) {
      case X:
      case Y:
      case Z:
        return NumbersUtils.isNumber(raw);
      case PITCH:
        float pitch = Float.parseFloat(raw);
        return NumbersUtils.isNumber(raw) && pitch >= -90 && pitch <= 90;
      case YAW:
        float yaw = Float.parseFloat(raw);
        return NumbersUtils.isNumber(raw) && yaw >= 0 && yaw < 360;
      case WORLD:
        return Bukkit.getWorld(raw) != null;
    }
    return false;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      Block targetBlock = player.getTargetBlock(100);
      
      Location targetLocation;
      
      if (targetBlock == null) {
        targetLocation = player.getLocation();
      } else {
        targetLocation = targetBlock.getLocation();
        targetLocation.setYaw(player.getLocation().getYaw());
        targetLocation.setPitch(player.getLocation().getPitch());
      }
      
      return part.getSuggestion(targetLocation);
    }
    return Collections.singletonList(part.name());
  }
  
  @Override
  public String argumentName() {
    return part.name();
  }
}
