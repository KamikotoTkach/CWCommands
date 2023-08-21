package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Particle;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleArg extends Argument {
  static List<String> particles = Arrays.stream(Particle.values()).map(Enum::name).collect(Collectors.toList());
  
  @Override
  public boolean valid(String raw) {
    return particles.contains(raw);
  }
  
  @Override
  public List<String> completions(Sender sender) {
    return particles;
  }
  
  @Override
  public String argumentName() {
    return "частица";
  }
}
