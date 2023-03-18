package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;
import tkachgeek.commands.command.arguments.bukkit.location.LocationPart;
import tkachgeek.commands.command.arguments.bukkit.location.TargetXArg;

import java.util.List;

public class ComplexArg extends Argument {
  public static ComplexArg xyz = new ComplexArg(new TargetXArg(LocationPart.X), new TargetXArg(LocationPart.Y), new TargetXArg(LocationPart.Z));
  public static ComplexArg xyzw = new ComplexArg(new TargetXArg(LocationPart.X), new TargetXArg(LocationPart.Y), new TargetXArg(LocationPart.Z), new TargetXArg(LocationPart.WORLD));
  
  List<Argument> args;
  
  public ComplexArg(Argument... args) {
    this.args = List.of(args);
  }
  
  public List<Argument> getArgs() {
    return args;
  }
  
  @Override
  public boolean valid(String raw) {
    return false;
  }
  
  @Override
  public List<String> completions(CommandSender sender) {
    return null;
  }
  
  @Override
  public String argumentName() {
    return null;
  }
}
