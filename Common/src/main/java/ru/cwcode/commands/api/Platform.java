package ru.cwcode.commands.api;

import ru.cwcode.commands.Command;

public abstract class Platform {
   public abstract void registerCommand(Command command);

   public abstract Logger getLogger();
}
