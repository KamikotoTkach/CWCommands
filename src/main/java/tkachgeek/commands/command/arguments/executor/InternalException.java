package tkachgeek.commands.command.arguments.executor;

import net.kyori.adventure.text.Component;

public class InternalException extends Exception {
  private final Component componentMessage;
  
  public InternalException(Component message) {
    this.componentMessage = message;
  }
  
  public Component getComponentMessage() {
    return componentMessage;
  }
}
