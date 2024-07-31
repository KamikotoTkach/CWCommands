package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.RawCommand;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.Completion;
import ru.cwcode.cwutils.text.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VelocityCommand implements RawCommand {
   VelocityTabCompleter tabCompleter;
   VelocityCommandParser commandParser;
   String permission;
   
   public VelocityCommand(Command command) {
      tabCompleter = new VelocityTabCompleter(command);
      commandParser = new VelocityCommandParser(command);
      permission = command.getPermission();
   }
   
   @Override
   public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
      return CompletableFuture.supplyAsync(() -> suggest(invocation));
   }
   
   @Override
   public List<String> suggest(Invocation invocation) {
      VelocitySender sender = new VelocitySender(invocation.source());
      
      String[] args = StringUtils.safetySplit(invocation.arguments(), " ");
      
      return this.tabCompleter.onTabComplete(sender, args).stream()
                              .map(Completion::suggestion)
                              .toList();
   }
   
   @Override
   public boolean hasPermission(Invocation invocation) {
      VelocitySender sender = new VelocitySender(invocation.source());
      
      return sender.hasPermission(permission);
   }
   
   @Override
   public void execute(Invocation invocation) {
      VelocitySender sender = new VelocitySender(invocation.source());
      String label = invocation.alias();
      String[] args = StringUtils.safetySplit(invocation.arguments(), " ");

      this.commandParser.onCommandExecute(sender, label, args);
   }

   public VelocityTabCompleter getTabCompleter() {
      return tabCompleter;
   }

   public VelocityCommandParser getCommandParser() {
      return commandParser;
   }
}
