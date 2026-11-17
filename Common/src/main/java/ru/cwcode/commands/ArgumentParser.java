package ru.cwcode.commands;

import java.util.Arrays;

public class ArgumentParser {
  /**
   * Копии аргументов набора со значениями этого вызова; в конце — пропущенные опциональные аргументы,
   * у которых задано значение по умолчанию.
   */
  public final Argument[] args;

  public ArgumentParser(String[] raw, ArgumentSet set) {
    Argument[] template = set.arguments;

    if (set.spacedLastArgument && raw.length > template.length) {
      String[] copy = Arrays.copyOfRange(raw, 0, template.length);
      copy[template.length - 1] = String.join(" ", Arrays.copyOfRange(raw, template.length - 1, raw.length));
      raw = copy;
    }

    Argument[] defaults = set.omittedDefaults;
    this.args = new Argument[template.length + defaults.length];

    for (int i = 0; i < template.length; i++) {
      args[i] = template[i].withRaw(i < raw.length ? raw[i] : null);
    }

    for (int i = 0; i < defaults.length; i++) {
      args[template.length + i] = defaults[i].withRaw(defaults[i].raw);
    }
  }

  public Argument get(int index) {
    if (isPresent(index)) return args[index];
    return null;
  }

  public Argument get(String tag) {
    for (Argument arg : args) {
      if (arg.getTag().equals(tag)) return arg;
    }
    return null;
  }

  public boolean isPresent(int index) {
    return index >= 0 && index < args.length;
  }
}
