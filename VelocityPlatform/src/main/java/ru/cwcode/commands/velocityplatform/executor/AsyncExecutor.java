package ru.cwcode.commands.velocityplatform.executor;

import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;

public abstract class AsyncExecutor extends Executor {
   @Override
   public void prepare(Sender sender, String[] args, ArgumentSet argumentSet, Command command) {
      this.parser = new ArgumentParser(args, argumentSet);
      this.sender = sender;
      this.command = command;

      VelocityMain.getPlatform().getServer().getScheduler().buildTask(VelocityMain.getPlatform().getPlugin(), () -> {
         try {
            if (sender.isPlayer()) {
               executeForPlayer();
            } else {
               executeForNonPlayer();
            }
         } catch (Exception e) {
            handleError(e);
         }
      });
   }
}
