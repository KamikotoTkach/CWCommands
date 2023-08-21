package ru.cwcode.commands.paperplatform.paper;

import ru.cwcode.commands.api.Logger;

public class PaperLogger implements Logger {
   private final java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

   @Override
   public void info(String message) {
      this.logger.info(message);
   }

   @Override
   public void warn(String message) {
      this.logger.warning(message);
   }

   @Override
   public void error(String message) {
      this.warn(message);
   }
}
