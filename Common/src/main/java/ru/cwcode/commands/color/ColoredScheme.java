package ru.cwcode.commands.color;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

public class ColoredScheme implements ColorGenerationStrategy {
  private final TextColor main;
  private final TextColor argument;
  private final TextColor optional;
  private final TextColor subcommand;
  private final TextColor written;
  private final TextColor permissions;
  private final TextColor accent;
  private final TextColor argumentWithNoPermissions;
  private final TextColor optionalWithNoPermissions;
  private final TextColor subcommandWithNoPermissions;
  private final TextColor writtenWithNoPermissions;
  private final TextColor permissionsWithNoPermissions;
  private final TextColor accentWithNoPermissions;
  
  public ColoredScheme(TextColor baseColor, TextColor baseNoPermissionColor) {
    this.main = generateColor(baseColor, DefaultColorGenerationStrategy.main);
    
    this.argument = generateColor(baseColor, DefaultColorGenerationStrategy.argument);
    this.optional = generateColor(baseColor, DefaultColorGenerationStrategy.optional);
    this.subcommand = generateColor(baseColor, DefaultColorGenerationStrategy.subcommand);
    this.written = generateColor(baseColor, DefaultColorGenerationStrategy.written);
    this.permissions = generateColor(baseColor, DefaultColorGenerationStrategy.permissions);
    this.accent = generateColor(baseColor, DefaultColorGenerationStrategy.accent);
    
    this.argumentWithNoPermissions = generateColor(baseNoPermissionColor, DefaultColorGenerationStrategy.argumentWithNoPermissions);
    this.optionalWithNoPermissions = generateColor(baseNoPermissionColor, DefaultColorGenerationStrategy.optionalWithNoPermissions);
    this.subcommandWithNoPermissions = generateColor(baseNoPermissionColor, DefaultColorGenerationStrategy.subcommandWithNoPermissions);
    this.writtenWithNoPermissions = generateColor(baseNoPermissionColor, DefaultColorGenerationStrategy.writtenWithNoPermissions);
    this.permissionsWithNoPermissions = generateColor(baseNoPermissionColor, DefaultColorGenerationStrategy.permissionsWithNoPermissions);
    this.accentWithNoPermissions = generateColor(baseNoPermissionColor, DefaultColorGenerationStrategy.accentWithNoPermissions);
  }
  
  public ColoredScheme(TextColor baseColor) {
    this(baseColor, baseColor);
  }
  
  private TextColor generateColor(TextColor h, TextColor sv) {
    HSVLike hsvH = h.asHSV();
    HSVLike hsvSV = sv.asHSV();
    
    HSVLike combined = HSVLike.of(hsvH.h(), hsvSV.s(), hsvSV.v());
    
    return TextColor.color(combined);
  }
  
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
