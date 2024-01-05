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

    main = generateColor(baseColor, TextColor.fromHexString("#00a6f0"));
    
    argument = generateColor(baseColor, TextColor.fromHexString("#00baff"));
    optional = generateColor(baseColor, TextColor.fromHexString("#02d7ff"));
    subcommand = generateColor(baseColor, TextColor.fromHexString("#0098dc"));
    written = generateColor(baseColor, TextColor.fromHexString("#007ab5"));
    permissions = generateColor(baseColor, TextColor.fromHexString("#055080"));
    accent = generateColor(baseColor, TextColor.fromHexString("#8adaff"));
    
    argumentWithNoPermissions = generateColor(baseNoPermissionColor, TextColor.fromHexString("#FF2C00"));
    optionalWithNoPermissions = generateColor(baseNoPermissionColor, TextColor.fromHexString("#FF1800"));
    subcommandWithNoPermissions = generateColor(baseNoPermissionColor, TextColor.fromHexString("#FF2C00"));
    writtenWithNoPermissions = generateColor(baseNoPermissionColor, TextColor.fromHexString("#FF3100"));
    permissionsWithNoPermissions = generateColor(baseNoPermissionColor, TextColor.fromHexString("#C83000"));
    accentWithNoPermissions = generateColor(baseNoPermissionColor, TextColor.fromHexString("#FF9C84"));
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
