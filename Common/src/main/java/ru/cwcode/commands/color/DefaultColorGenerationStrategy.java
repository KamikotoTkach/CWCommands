package ru.cwcode.commands.color;

import net.kyori.adventure.text.format.TextColor;
import ru.cwcode.commands.api.CommandsAPI;

public class DefaultColorGenerationStrategy implements ColorGenerationStrategy {
  static final TextColor main = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.main"));
  static final TextColor argument = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.argument"));
  static final TextColor optional = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.optional"));
  static final TextColor subcommand = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.subcommand"));
  static final TextColor written = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.written"));
  static final TextColor permissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.permissions"));
  static final TextColor accent = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.accent"));
  static final TextColor argumentWithNoPermissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.argumentWithNoPermissions"));
  static final TextColor optionalWithNoPermissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.optionalWithNoPermissions"));
  static final TextColor subcommandWithNoPermissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.subcommandWithNoPermissions"));
  static final TextColor writtenWithNoPermissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.writtenWithNoPermissions"));
  static final TextColor permissionsWithNoPermissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.permissionsWithNoPermissions"));
  static final TextColor accentWithNoPermissions = TextColor.fromHexString(CommandsAPI.getConfig().get("color.default.accentWithNoPermissions"));
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
