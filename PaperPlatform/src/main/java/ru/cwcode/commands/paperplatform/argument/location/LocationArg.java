package ru.cwcode.commands.paperplatform.argument.location;

import ru.cwcode.commands.arguments.ComplexArg;

public class LocationArg {
  public static ComplexArg xyz = new ComplexArg(new TargetXArg(LocationPart.X), new TargetXArg(LocationPart.Y), new TargetXArg(LocationPart.Z));
  public static ComplexArg xyzw = new ComplexArg(new TargetXArg(LocationPart.X), new TargetXArg(LocationPart.Y), new TargetXArg(LocationPart.Z), new TargetXArg(LocationPart.WORLD));
  
}
