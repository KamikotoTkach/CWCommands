package tkachgeek.commands.command.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.arguments.ComplexArg;
import tkachgeek.commands.command.arguments.ExactStringArg;
import tkachgeek.commands.command.arguments.bukkit.location.LocationPart;
import tkachgeek.commands.command.arguments.bukkit.location.TargetXArg;

public class main extends JavaPlugin {
  @Override
  public void onEnable() {
    new Command("commandsTest", "*")
       .arguments(
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetXYZ"), ComplexArg.xyz),
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetX"), new TargetXArg(LocationPart.X)),
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetY"), new TargetXArg(LocationPart.Y)),
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetZ"), new TargetXArg(LocationPart.Z)),
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetPITCH"), new TargetXArg(LocationPart.PITCH)),
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetYAW"), new TargetXArg(LocationPart.YAW)),
          new ArgumentSet(new EmptyExecutor(), new ExactStringArg("targetWORLD"), new TargetXArg(LocationPart.WORLD))
       ).register(this);
  }
}
