package tkachgeek.commands.command.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.ComplexArg;
import tkachgeek.commands.command.arguments.ExactStringArg;
import tkachgeek.commands.command.arguments.bukkit.location.LocationPart;
import tkachgeek.commands.command.arguments.bukkit.location.TargetXArg;
import tkachgeek.commands.command.arguments.datetime.DurationArg;

public class main extends JavaPlugin {
  @Override
  public void onEnable() {
    
    new Command("commandsTest", "*")
       .arguments(
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetXYZ"), ComplexArg.xyz),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetX"), new TargetXArg(LocationPart.X)),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetY"), new TargetXArg(LocationPart.Y)),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetZ"), new TargetXArg(LocationPart.Z)),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetPITCH"), new TargetXArg(LocationPart.PITCH)),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetYAW"), new TargetXArg(LocationPart.YAW)),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("targetWORLD"), new TargetXArg(LocationPart.WORLD)),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("duration"), new DurationArg())
       ).register(this);
  }
}
