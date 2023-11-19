package ru.cwcode.commands.api;

public class CommandsAPI {
  static Platform registeredPlatform;
  public static Platform getPlatform() {
    return registeredPlatform;
  }
  
  public static void setPlatform(Platform platform) {
    if(registeredPlatform == null) registeredPlatform = platform;
  }
}
