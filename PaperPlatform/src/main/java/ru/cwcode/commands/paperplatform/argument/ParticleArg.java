package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Particle;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class ParticleArg extends Argument {
  static Set<String> particles = Arrays.stream(Particle.values()).map(x -> x.name()).collect(Collectors.toSet());
  
  @Override
  public boolean valid(String raw) {
    return particles.contains(raw);
  }
  
  @Override
  public Collection<String> completions(Sender sender) {
    return particles;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.particle.name");
  }
}
