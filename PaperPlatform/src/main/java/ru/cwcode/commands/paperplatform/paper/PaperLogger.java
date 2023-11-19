package ru.cwcode.commands.paperplatform.paper;

import ru.cwcode.commands.api.Logger;
import ru.cwcode.commands.paperplatform.PaperMain;

public class PaperLogger implements Logger {
   private final java.util.logging.Logger logger = PaperMain.plugin.getLogger();

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
