package ru.cwcode.commands.paperplatform.argument.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.paper.PaperSender;
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
  public List<String> completions(Sender sender) {
    if (sender instanceof PaperSender) {
      Player player = ((PaperSender) sender).getPlayer();
      Block targetBlock = player.getTargetBlockExact(100);
      
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
  
  @Override
  public Object map() {
    switch (part) {
      case X:
      case Y:
      case Z:
        return toDouble();
      case PITCH:
      case YAW:
        return toFloat();
      case WORLD:
        return Bukkit.getWorld(part.name());
      default:
        throw new IllegalArgumentException("Unknown location part: " + part);
    }
  }
}
