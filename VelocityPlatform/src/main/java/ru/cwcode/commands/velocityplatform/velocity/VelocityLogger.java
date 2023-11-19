package ru.cwcode.commands.velocityplatform.velocity;

import ru.cwcode.commands.api.Logger;

public class VelocityLogger implements Logger {
   private final org.slf4j.Logger logger;

   public VelocityLogger(org.slf4j.Logger logger) {
      this.logger = logger;
   }

   @Override
   public void info(String message) {

   }

   @Override
   public void warn(String message) {

   }

   @Override
   public void error(String message) {

   }
}
