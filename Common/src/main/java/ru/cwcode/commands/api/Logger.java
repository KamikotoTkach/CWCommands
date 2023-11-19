package ru.cwcode.commands.api;

public interface Logger {

   void info(String message);

   void warn(String message);

   void error(String message);
}
