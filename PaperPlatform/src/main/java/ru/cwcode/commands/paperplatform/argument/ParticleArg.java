package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Particle;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleArg extends EnumArg {
  
  public ParticleArg() {
    super(Particle.values(), "частица");
  }
}
