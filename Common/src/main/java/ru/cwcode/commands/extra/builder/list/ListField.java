package ru.cwcode.commands.extra.builder.list;

import net.kyori.adventure.text.Component;
import ru.cwcode.commands.api.Sender;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ListField<E, S extends Sender> {
  String name;
  BiFunction<E, S, Component> text = (e, s) -> Component.empty();
  BiFunction<E, S, Component> hover = null;
  BiConsumer<E, S> onClick = null;
  BiFunction<E, S, Boolean> shouldShow = (e, s) -> true;
  
  public ListField<E, S> setName(String name) {
    this.name = name;
    return this;
  }
  
  public ListField<E, S> setText(BiFunction<E, S, Component> text) {
    this.text = text;
    return this;
  }
  
  public ListField<E, S> setHover(BiFunction<E, S, Component> hover) {
    this.hover = hover;
    return this;
  }
  
  public ListField<E, S> setOnClick(BiConsumer<E, S> onClick) {
    this.onClick = onClick;
    return this;
  }
  
  public ListField<E, S> setShouldShow(BiFunction<E, S, Boolean> shouldShow) {
    this.shouldShow = shouldShow;
    return this;
  }
}
