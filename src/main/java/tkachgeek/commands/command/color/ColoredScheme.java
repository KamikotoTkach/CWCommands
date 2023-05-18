package tkachgeek.commands.command.color;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

public class ColoredScheme implements ColorGenerationStrategy {
  TextColor selectedColor;
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
  
  public ColoredScheme(TextColor selectedColor) {
    this.selectedColor = selectedColor;
    
    main = generateColor(TextColor.fromHexString("#00a6f0"));
    argument = generateColor(TextColor.fromHexString("#00baff"));
    optional = generateColor(TextColor.fromHexString("#02d7ff"));
    subcommand = generateColor(TextColor.fromHexString("#0098dc"));
    written = generateColor(TextColor.fromHexString("#007ab5"));
    permissions = generateColor(TextColor.fromHexString("#055080"));
    accent = generateColor(TextColor.fromHexString("#8adaff"));
    argumentWithNoPermissions = generateColor(TextColor.fromHexString("#FF2C00"));
    optionalWithNoPermissions = generateColor(TextColor.fromHexString("#FF1800"));
    subcommandWithNoPermissions = generateColor(TextColor.fromHexString("#FF2C00"));
    writtenWithNoPermissions = generateColor(TextColor.fromHexString("#FF3100"));
    permissionsWithNoPermissions = generateColor(TextColor.fromHexString("#C83000"));
    accentWithNoPermissions = generateColor(TextColor.fromHexString("#FF9C84"));
  }
  
  private TextColor generateColor(TextColor base) {
    HSVLike selectedHSV = selectedColor.asHSV();
    HSVLike baseHSV = base.asHSV();
    
    HSVLike of = HSVLike.of(selectedHSV.h(), baseHSV.s(), baseHSV.v());
    
    return TextColor.color(of);
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
