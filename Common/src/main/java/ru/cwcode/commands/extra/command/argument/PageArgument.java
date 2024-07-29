package ru.cwcode.commands.extra.command.argument;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.RepositoryAccessor;
import ru.cwcode.cwutils.numbers.NumbersUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageArgument<E, K, S extends Sender> extends Argument {
  
  RepositoryAccessor<E, K, S> builder;
  
  public PageArgument(RepositoryAccessor<E, K, S> builder) {
    this.builder = builder;
  }
  
  @Override
  public boolean valid(String raw) {
    return false;
  }
  
  @Override
  public boolean valid(Sender sender, String raw, List<String> arguments) {
    try {
      if (!NumbersUtils.isInteger(raw)) return false;
      
      int parsed = Integer.parseInt(raw);
      return parsed > 0 && parsed <= builder.keys((S) sender).size();
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  @Override
  public Collection<String> completions(Sender sender) {
    return IntStream.range(1, builder.keys((S) sender).size() + 1)
                    .mapToObj(operand -> operand + "")
                    .collect(Collectors.toList());
  }
  
  @Override
  public String argumentName() {
    return "страница";
  }
}
