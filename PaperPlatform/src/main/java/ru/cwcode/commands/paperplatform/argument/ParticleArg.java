package ru.cwcode.commands.paperplatform.argument;
import org.bukkit.Particle;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.EnumArg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class ParticleArg extends EnumArg {
  
  public ParticleArg() {
    super(Particle.values(), l10n.get("argument.particle.name"));
  }
}
