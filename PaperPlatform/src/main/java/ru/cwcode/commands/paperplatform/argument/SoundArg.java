package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Sound;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class SoundArg extends Argument {
  // Sound became a registry on 1.21.3+ and its name() is the key ("ENTITY.ARROW.HIT"); the enum form ("ENTITY_ARROW_HIT") is the one both eras accept
  static Set<String> sounds = Arrays.stream(Sound.values()).map(x -> x.name().replace('.', '_')).collect(Collectors.toSet());
  
  @Override
  public boolean valid(String raw) {
    return sounds.contains(raw);
  }
  
  @Override
  public Collection<String> completions(Sender sender) {
    return sounds;
  }
  
  @Override
  public String argumentName() {
    return l10n.get("argument.sound.name");
  }
}
