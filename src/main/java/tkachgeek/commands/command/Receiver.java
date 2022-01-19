package tkachgeek.commands.command;

import java.util.List;

@FunctionalInterface
public interface Receiver {
  List<String> receive();
}
