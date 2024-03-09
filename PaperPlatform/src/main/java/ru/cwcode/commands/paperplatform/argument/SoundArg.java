package ru.cwcode.commands.paperplatform.argument;

import org.bukkit.Sound;
import ru.cwcode.commands.arguments.EnumArg;

public class SoundArg extends EnumArg {
  public SoundArg() {
    super(Sound.values(), "звук");
  }
}
