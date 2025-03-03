package ru.cwcode.commands.extra.builder.list;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ru.cwcode.commands.ArgumentParser;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.arguments.basic.StringArg;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.commands.extra.builder.CommandBuilder;
import ru.cwcode.commands.extra.builder.update.UpdateCommandBuilder;
import ru.cwcode.commands.extra.command.argument.PageArgument;
import ru.cwcode.commands.extra.command.executor.ExtraExecutor;
import ru.cwcode.commands.extra.command.executor.SimpleExecutor;
import ru.cwcode.commands.preconditions.impl.HiddenCommandPrecondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ListCommandBuilder<E, K, S extends Sender> extends CommandBuilder<E, K, S> {
  List<ListField<E, S>> fields = new ArrayList<>();
  int pageSize = 10;
  Component separator = Component.text(" ");
  ListDecorator<S> listDecorator = new ListDecorator<>();
  String name = "list";
  Command command;
  boolean showIndex = false;
  Consumer<ArgumentSet> argumentSetConsumer = __ -> {};
  
  public ListCommandBuilder(RepositoryAccessor<E, K, S> builder) {
    super(builder);
  }
  
  public ListCommandBuilder<E, K, S> argumentSetEdit(Consumer<ArgumentSet> argumentSet) {
    this.argumentSetConsumer = argumentSet;
    return this;
  }
  
  public ListCommandBuilder<E, K, S> showIndex(boolean showIndex) {
    this.showIndex = showIndex;
    return this;
  }
  
  public ListCommandBuilder<E, K, S> field(Consumer<ListField<E, S>> fieldConsumer) {
    ListField<E, S> field = new ListField<>();
    
    fieldConsumer.accept(field);
    fields.add(field);
    
    return this;
  }
  
  public ListCommandBuilder<E, K, S> decorate(Consumer<ListDecorator<S>> listDecorator) {
    listDecorator.accept(this.listDecorator);
    
    return this;
  }
  
  public ListCommandBuilder<E, K, S> separator(Component separator) {
    this.separator = separator;
    return this;
  }
  
  public ListCommandBuilder<E, K, S> name(String name) {
    this.name = name;
    return this;
  }
  
  public ListCommandBuilder<E, K, S> pageSize(int pageSize) {
    this.pageSize = pageSize;
    return this;
  }
  
  @Override
  public void register(Command command) {
    this.command = command;
    
    ArgumentSet clickHandler = new ArgumentSet(new ExtraExecutor<>(repositoryAccessor, this::click, 1), new ExactStringArg("handleClick"), repositoryAccessor.keyArgument(), new StringArg())
      .hidden()
      .preconditions(new HiddenCommandPrecondition());
    
    ArgumentSet list = new ArgumentSet(new SimpleExecutor<>(this::showList),
                                              new ExactStringArg(name),
                                              new PageArgument<>(repositoryAccessor).optional());
    
    argumentSetConsumer.accept(clickHandler);
    argumentSetConsumer.accept(list);
    
    command.arguments(clickHandler, list);
  }
  
  private void click(S sender, E element, ArgumentParser argumentParser) {
    String field = argumentParser.get(2).toString();
    
    for (ListField<E, S> listField : fields) {
      if (listField.name.equals(field)) {
        listField.onClick.accept(element, sender);
        return;
      }
    }
  }
  
  private void showList(S sender, ArgumentParser argumentParser) {
    int page = argumentParser.isPresent(1) ? argumentParser.get(1).toInt() : 1;
    int skip = (page - 1) * pageSize;
    int limit = pageSize;
    int currentIndex = 0;
    
    Collection<K> keys = repositoryAccessor.keys(sender);
    
    ListContext listContext = new ListContext(command, page, (int) Math.ceil((double) keys.size() / pageSize));
    
    Component header = listDecorator.header.apply(listContext, sender);
    if (header != null) sender.sendMessage(header);
    
    for (K key : keys) {
      currentIndex++;
      
      if (skip > 0) {
        skip--;
        continue; //не лучшее решение, но без выделения лишней памяти
      }
      
      if (limit == 0) break;
      limit--;
      
      E element = repositoryAccessor.keyToElement(sender, key);
      
      Component row = Component.empty();
      
      if (showIndex) {
        row = row.append(listDecorator.indexField.apply(listContext, currentIndex, sender));
      }
      
      for (ListField<E, S> field : fields) {
        if (!field.shouldShow.apply(element, sender)) continue;
        
        Component text = field.text.apply(element, sender);
        
        Component hover = field.hover == null ? null : field.hover.apply(element, sender);
        BiConsumer<E, S> onClick = field.onClick;
        
        if (hover != null) {
          text = text.hoverEvent(HoverEvent.showText(hover));
        }
        
        if (onClick != null) {
          String commandStr = String.format("/%s handleClick %s %s",
                                            command.getFullCommandPath(command.getRootCommand().getName()),
                                            repositoryAccessor.keyToString(key),
                                            field.name);
          
          text = text.clickEvent(ClickEvent.runCommand(commandStr));
        }
        
        if (!row.equals(Component.empty())) {
          row = row.append(separator);
        }
        
        row = row.append(text);
      }
      
      sender.sendMessage(row);
    }
    
    if (limit == pageSize) {
      Component noElementsMessage = listDecorator.noElements.apply(listContext, sender);
      if (noElementsMessage != null) sender.sendMessage(noElementsMessage);
    }
    
    Component footer = listDecorator.footer.apply(listContext, sender);
    if (footer != null) sender.sendMessage(footer);
  }
}
