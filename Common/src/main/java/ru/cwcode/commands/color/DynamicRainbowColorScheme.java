package ru.cwcode.commands.color;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

public class DynamicRainbowColorScheme implements ColorGenerationStrategy {
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
  public static DynamicRainbowColorScheme INSTANCE = new DynamicRainbowColorScheme();
  
  @Override
  
  public TextColor main() {
    return wrap(main);
  }
  
  @Override
  public TextColor argument(boolean canPerformedBy) {
    return wrap(canPerformedBy ? argument : argumentWithNoPermissions);
  }
  
  @Override
  public TextColor optional(boolean canPerformedBy) {
    return wrap(canPerformedBy ? optional : optionalWithNoPermissions);
  }
  
  @Override
  public TextColor subcommand(boolean canPerformedBy) {
    return wrap(canPerformedBy ? subcommand : subcommandWithNoPermissions);
  }
  
  @Override
  public TextColor written(boolean canPerformedBy) {
    return wrap(canPerformedBy ? written : writtenWithNoPermissions);
  }
  
  @Override
  public TextColor permissions(boolean canPerformedBy) {
    return wrap(canPerformedBy ? permissions : permissionsWithNoPermissions);
  }
  
  @Override
  public TextColor accent(boolean canPerformedBy) {
    return wrap(canPerformedBy ? accent : accentWithNoPermissions);
  }
  
  public TextColor wrap(TextColor color) {
    HSVLike hsvLike = color.asHSV();
    long time = System.nanoTime() % 3600000;
    
    float h = time / 3600000.0f;
    float sOffset = h > 0.6 && h < 0.8 ? -0.3f : 0f;
    HSVLike of = HSVLike.of(h, hsvLike.s() + sOffset, Math.min(1, hsvLike.v() - sOffset));
    
    return TextColor.color(of);
  }
}
