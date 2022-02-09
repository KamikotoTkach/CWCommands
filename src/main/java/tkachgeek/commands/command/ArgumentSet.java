package tkachgeek.commands.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tkachgeek.commands.command.arguments.executor.Executor;
import tkachgeek.commands.command.arguments.spaced.SpacedArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ArgumentSet {
  protected final Argument[] arguments;
  final Executor executor;
  String permission;
  boolean spacedLastArgument = false;
  boolean blockForPlayers = false;
  boolean blockForNonPlayers = false;
  Predicate<CommandSender> canExecute = x -> true;
  int optionalStart;
  
  public ArgumentSet(Executor executor, String permission, Argument... arguments) {
    this.arguments = arguments;
    this.executor = executor;
    this.permission = permission;
    int pos = 0;
    int len = arguments.length - 1;
    int optionalStart = -1;
    for (Argument argument : arguments) {
      if (argument instanceof SpacedArgument) {
        if (pos != len) {
          Logger.getGlobal().warning("Аргумент " + argument.getClass().getName() + " должен быть последним в списке аргументов");
        } else {
          spacedLastArgument = true;
        }
      }
      if (argument.isOptional()) {
        if (optionalStart == -1) {
          optionalStart = pos;
        }
      } else {
        if (optionalStart != -1) {
          Logger.getGlobal().warning("Аргумент " + argument.getClass().getName() + " не может быть не опциональным, поскольку перед ним уже был опциональный");
          optionalStart = -10;
        }
      }
      pos++;
    }
    this.optionalStart = optionalStart;
  }
  
  public ArgumentSet canExecute(Predicate<CommandSender> canExecute) {
    this.canExecute = canExecute;
    return this;
  }
  
  private void sendBlockedArgumentWarning() {
    Bukkit.getLogger().warning("Набор агрументов " + Arrays.stream(arguments)
       .map(Argument::argumentName)
       .collect(Collectors.joining(", ")) + " не может быть выполнен");
  }
  
  public ArgumentSet blockForPlayers() {
    blockForPlayers = true;
    if (blockForNonPlayers) sendBlockedArgumentWarning();
    return this;
  }
  
  public ArgumentSet blockForNonPlayers() {
    blockForNonPlayers = true;
    if (blockForPlayers) sendBlockedArgumentWarning();
    return this;
  }
  
  protected boolean isArgumentsFit(String... args) {
    
    if (args.length != arguments.length && !spacedLastArgument) return false;
    if (args.length < arguments.length) return false;
    
    if (spacedLastArgument) {
      String[] copy = Arrays.copyOfRange(args, 0, arguments.length);
      copy[arguments.length - 1] = String.join(" ", Arrays.copyOfRange(args, arguments.length - 1, args.length));
      args = copy;
    }
    
    for (int i = 0; i < args.length; i++) {
      if (!arguments[i].valid(args[i], Arrays.asList(args))) return false;
    }
    return true;
  }
  
  protected boolean canPerformedBy(CommandSender sender) {
    if (sender instanceof Player) {
      if (blockForPlayers) return false;
    } else {
      if (blockForNonPlayers) return false;
    }
    return canExecute.test(sender) && (permission.isEmpty() || sender.hasPermission(permission) || sender.isOp());
  }
  
  protected List<String> getCompletesFor(List<String> written, CommandSender sender) {
    int skip = 0;
    List<String> completes = new ArrayList<>();
    
    if (arguments.length == 0) return completes;
    
    if (spacedLastArgument && arguments.length < written.size()) {
      skip = written.size() - arguments.length;
      String writtenLastSpacedString = String.join(" ", written.subList(arguments.length - 1, written.size()));
      written = written.subList(0, arguments.length);
      written.set(arguments.length - 1, writtenLastSpacedString);
    }
    
    if (arguments.length >= written.size()) {
      
      for (int i = 0; i < written.size() - 1; i++) {
        if (!arguments[i].valid(written.get(i), written)) return completes;
      }
      
      int finalSkip = skip;
      
      return arguments[written.size() - 1].completions(sender, written).stream()
         .map(x -> {
           if (finalSkip > 0) {
             List<String> parts = List.of(x.split(" "));
             return String.join(" ", parts.subList(finalSkip, parts.size()));
           } else return x;
         }).collect(Collectors.toList());
    }
    return completes;
  }
}
