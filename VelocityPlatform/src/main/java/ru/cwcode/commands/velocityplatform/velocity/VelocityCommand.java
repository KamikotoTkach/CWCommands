package ru.cwcode.commands.velocityplatform.velocity;

import com.velocitypowered.api.command.RawCommand;
import ru.cwcode.commands.Command;
import tkachgeek.tkachutils.text.StringUtils;

import java.util.List;

public class VelocityCommand implements RawCommand {
   VelocityTabCompleter tabCompleter;
   VelocityCommandParser commandParser;

   public VelocityCommand(Command command) {
      tabCompleter = new VelocityTabCompleter(command);
      commandParser = new VelocityCommandParser(command);
   }

   @Override
   public List<String> suggest(Invocation invocation) {
      VelocitySender sender = new VelocitySender(invocation.source());
      String[] args = StringUtils.safetySplit(invocation.arguments(), " ");
      return this.tabCompleter.onTabComplete(sender, args);
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
