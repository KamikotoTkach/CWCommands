package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Sound;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SoundArg extends Argument {
  static List<String> sounds = Arrays.stream(Sound.values()).map(Enum::name).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return sounds.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return sounds;
  }
  
  @Override
  public String argumentName() {
    return "звук";
  }
}
