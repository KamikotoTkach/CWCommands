package ru.cwcode.commands.api;

import ru.cwcode.cwutils.l10n.L10n;

public class CommandsAPI {
  public static L10n l10n;
  static Platform registeredPlatform;
  
  public static Platform getPlatform() {
    return registeredPlatform;
  }
  
  public static void setL10n(L10n l10n) {
    CommandsAPI.l10n = l10n;
  }
  
  public static void setPlatform(Platform platform) {
    if (registeredPlatform == null) registeredPlatform = platform;
  }
}
