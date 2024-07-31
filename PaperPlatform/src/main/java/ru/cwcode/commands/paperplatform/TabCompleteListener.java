package ru.cwcode.commands.paperplatform;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.cwcode.commands.Completion;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.paperplatform.paper.PaperPlatform;
import ru.cwcode.commands.paperplatform.paper.PaperSender;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteListener implements Listener {
  @EventHandler
  void onAsyncTabCompleteEvent(AsyncTabCompleteEvent event) {
    
    String[] commandWithArgs = event.getBuffer()
                                    .substring(1)
                                    .concat("%")
                                    .split(" ");
    
    int lastArgIndex = commandWithArgs.length - 1;
    
    { //добавляем в конец пустой аргумент если нужно
      String lastArg = commandWithArgs[lastArgIndex];
      
      if (lastArg.equals("%")) {
        commandWithArgs[lastArgIndex] = "";
      } else {
        commandWithArgs[lastArgIndex] = lastArg.substring(0, lastArg.length() - 1);
      }
    }
    
    String command = commandWithArgs[0];
    
    ((PaperPlatform) CommandsAPI.getPlatform()).getRegisteredCommand(command)
                                               .ifPresent(paperCommand -> {
                                                 String[] args = new String[lastArgIndex];
                                                 System.arraycopy(commandWithArgs, 1, args, 0, lastArgIndex);
                                                 
                                                 List<Completion> strings = paperCommand.getTabCompleter().onTabComplete(new PaperSender(event.getSender()), args);
                                                 event.completions(strings.stream().map(x -> AsyncTabCompleteEvent.Completion.completion(x.suggestion(), x.tooltip())).collect(Collectors.toList()));
                                                 event.setHandled(true);
                                               });
  }
}
