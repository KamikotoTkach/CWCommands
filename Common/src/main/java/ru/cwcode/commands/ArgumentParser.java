package ru.cwcode.commands;

import java.util.Arrays;

public class ArgumentParser {
  public final Argument[] args;
  
  public ArgumentParser(String[] raw, ArgumentSet set) {
    this.args = set.arguments;
    
    if (set.spacedLastArgument) {
      String[] copy = Arrays.copyOfRange(raw, 0, args.length);
      copy[args.length - 1] = String.join(" ", Arrays.copyOfRange(raw, args.length - 1, raw.length));
      raw = copy;
    }
    
    for (int i = 0; i < raw.length; i++) {
      args[i].raw = raw[i];
    }
  }
  
  public Argument get(int index) {
    if (isPresent(index)) return args[index];
    return null;
  }
  
  public Argument get(String tag) {
    for (Argument arg : args) {
      if (arg.getTag().equals(tag)) return arg;
    }
    
    return null;
  }
  
  public boolean isPresent(int index) {
    return index >= 0 && index < args.length;
  }
}
