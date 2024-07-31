package ru.cwcode.commands;

import net.kyori.adventure.text.Component;
import ru.cwcode.commands.color.ColorGenerationStrategy;

public class Completion {
  String suggestion;
  Component tooltip;
  
  public Completion(String suggestion, Component tooltip) {
    this.suggestion = suggestion;
    this.tooltip = tooltip;
  }
  
  public Completion(String suggestion, String tooltip, ColorGenerationStrategy color) {
    this.suggestion = suggestion;
    this.tooltip = (tooltip == null || tooltip.isEmpty()) ? null : Component.text(tooltip, color.main());
  }
  
  public Completion(Command command) {
    this(command.getName(), command.description(), command.getColorScheme());
  }
  
  public String suggestion() {
    return suggestion;
  }
  
  public Component tooltip() {
    return tooltip;
  }
}
