package ru.cwcode.commands.paperplatform.features;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

public class ItemToGsonCommand extends Executor {
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    String json = new Gson().toJson(player().getInventory().getItemInMainHand().serialize());
    
    player().sendMessage(Component.text(json)
                                  .clickEvent(ClickEvent.copyToClipboard(json))
                                  .hoverEvent(HoverEvent.showText(Component.text("<copy>"))));
  }
}
