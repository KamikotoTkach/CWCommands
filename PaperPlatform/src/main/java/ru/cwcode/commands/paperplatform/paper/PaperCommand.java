package ru.cwcode.commands.paperplatform.paper;

import ru.cwcode.commands.Command;

public class PaperCommand {
  PaperTabCompleter tabCompleter;
  PaperCommandParser commandParser;
  
  public PaperCommand(Command command) {
    tabCompleter = new PaperTabCompleter(command);
    commandParser = new PaperCommandParser(command);
  }
  
  public PaperTabCompleter getTabCompleter() {
    return tabCompleter;
  }
  
  public PaperCommandParser getCommandParser() {
    return commandParser;
  }
}
