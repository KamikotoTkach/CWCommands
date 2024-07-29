package ru.cwcode.commands.extra.builder.list;

import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.color.ColorGenerationStrategy;

public class ListContext {
  final Command command;
  final int pages;
  final int page;
  
  public ListContext(Command command, int page, int pages) {
    this.command = command;
    this.pages = pages;
    this.page = page;
  }
  
  public int pages() {
    return pages;
  }
  
  public int page() {
    return page;
  }
  
  public Command command() {
    return command;
  }
  
  public ColorGenerationStrategy colorScheme() {
    return command.getColorScheme();
  }
  
  public String pageCommand(int page) {
    return fullCommandPath() + " " + page;
  }
  
  public @NotNull String fullCommandPath() {
    return "/" + command.getFullCommandPath(command.getRootCommand().getName()) + " list";
  }
  
  public String nextPageCommand() {
    return pageCommand(Math.min(pages, page + 1));
  }
  
  public String prevPageCommand() {
    return pageCommand(Math.max(1, page - 1));
  }
  
  public String firstPageCommand() {
    return pageCommand(1);
  }
  
  public String lastPageCommand() {
    return pageCommand(pages);
  }
}
