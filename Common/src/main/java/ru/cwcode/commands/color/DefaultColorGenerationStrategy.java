package ru.cwcode.commands.color;

import net.kyori.adventure.text.format.TextColor;

public class DefaultColorGenerationStrategy implements ColorGenerationStrategy {
  private static final TextColor main = TextColor.fromHexString("#00a6f0");
  private static final TextColor argument = TextColor.fromHexString("#00baff");
  private static final TextColor optional = TextColor.fromHexString("#02d7ff");
  private static final TextColor subcommand = TextColor.fromHexString("#0098dc");
  private static final TextColor written = TextColor.fromHexString("#007ab5");
  private static final TextColor permissions = TextColor.fromHexString("#055080");
  private static final TextColor accent = TextColor.fromHexString("#8adaff");
  private static final TextColor argumentWithNoPermissions = TextColor.fromHexString("#FF2C00");
  private static final TextColor optionalWithNoPermissions = TextColor.fromHexString("#FF1800");
  private static final TextColor subcommandWithNoPermissions = TextColor.fromHexString("#FF2C00");
  private static final TextColor writtenWithNoPermissions = TextColor.fromHexString("#FF3100");
  private static final TextColor permissionsWithNoPermissions = TextColor.fromHexString("#C83000");
  private static final TextColor accentWithNoPermissions = TextColor.fromHexString("#FF9C84");
  public static DefaultColorGenerationStrategy INSTANCE = new DefaultColorGenerationStrategy();
  
  @Override
  
  public TextColor main() {
    return main;
  }
  
  @Override
  public TextColor argument(boolean canPerformedBy) {
    return canPerformedBy ? argument : argumentWithNoPermissions;
  }
  
  @Override
  public TextColor optional(boolean canPerformedBy) {
    return canPerformedBy ? optional : optionalWithNoPermissions;
  }
  
  @Override
  public TextColor subcommand(boolean canPerformedBy) {
    return canPerformedBy ? subcommand : subcommandWithNoPermissions;
  }
  
  @Override
  public TextColor written(boolean canPerformedBy) {
    return canPerformedBy ? written : writtenWithNoPermissions;
  }
  
  @Override
  public TextColor permissions(boolean canPerformedBy) {
    return canPerformedBy ? permissions : permissionsWithNoPermissions;
  }
  
  @Override
  public TextColor accent(boolean canPerformedBy) {
    return canPerformedBy ? accent : accentWithNoPermissions;
  }
}
