package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Sound;
import ru.cwcode.commands.arguments.EnumArg;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class SoundArg extends EnumArg {
  public SoundArg() {
    super(Sound.values(), l10n.get("argument.sound.name"));
  }
}
