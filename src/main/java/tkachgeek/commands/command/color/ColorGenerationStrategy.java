package tkachgeek.commands.command.color;

import net.kyori.adventure.text.format.TextColor;

public interface ColorGenerationStrategy {
  TextColor main();
  
  TextColor accent(boolean canPerformedBy);
  
  TextColor argument(boolean canPerformedBy);
  
  TextColor optional(boolean canPerformedBy);
  
  TextColor subcommand(boolean canPerformedBy);
  
  TextColor written(boolean canPerformedBy);
  
  TextColor permissions(boolean canPerformedBy);
}
