package ru.cwcode.commands.extra.builder.list;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.TriFunction;

import java.util.function.BiFunction;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class ListDecorator<S extends Sender> {
  BiFunction<ListContext, S, Component> header = (listContext, s) -> null;
  TriFunction<ListContext, Integer, S, Component> indexField = (listContext, index, s) -> Component.text(index + ".", listContext.colorScheme().written(true));
  BiFunction<ListContext, S, Component> noElements = (listContext, s) -> Component.text(l10n.get("extra.list.decorator.noElements"), listContext.colorScheme().accent(true));
  BiFunction<ListContext, S, Component> footer = (listContext, s) -> {
    return Component.text(" <<< ").clickEvent(ClickEvent.runCommand(listContext.firstPageCommand()))
                    .append(Component.text("< ").clickEvent(ClickEvent.runCommand(listContext.prevPageCommand())))
                    .append(Component.text(String.format("%s/%s ", listContext.page, listContext.pages))
                                     .clickEvent(ClickEvent.suggestCommand(listContext.fullCommandPath() + " ")))
                    .append(Component.text("> ").clickEvent(ClickEvent.runCommand(listContext.nextPageCommand())))
                    .append(Component.text(">>> ").clickEvent(ClickEvent.runCommand(listContext.lastPageCommand())))
                    .color(listContext.colorScheme().accent(true));
  };
  
  public ListDecorator<S> setHeader(BiFunction<ListContext, S, Component> header) {
    this.header = header;
    return this;
  }
  
  public ListDecorator<S> setIndexField(TriFunction<ListContext, Integer, S, Component> indexField) {
    this.indexField = indexField;
    return this;
  }
  
  public ListDecorator<S> setNoElements(BiFunction<ListContext, S, Component> noElements) {
    this.noElements = noElements;
    return this;
  }
  
  public ListDecorator<S> setFooter(BiFunction<ListContext, S, Component> footer) {
    this.footer = footer;
    return this;
  }
}
