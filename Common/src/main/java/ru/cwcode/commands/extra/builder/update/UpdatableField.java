package ru.cwcode.commands.extra.builder.update;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.TriConsumer;

public class UpdatableField<E, V, S extends Sender> {
  String name;
  Argument argument;
  TriConsumer<E, V, S> consumer;
  
  public UpdatableField(String name, Argument argument, TriConsumer<E, V, S> consumer) {
    this.name = name;
    this.argument = argument;
    this.consumer = consumer;
  }
}
