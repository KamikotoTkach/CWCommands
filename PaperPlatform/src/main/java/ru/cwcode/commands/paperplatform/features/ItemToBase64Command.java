package ru.cwcode.commands.paperplatform.features;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

import java.util.Base64;

public class ItemToBase64Command extends Executor {
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    String base64 = Base64.getEncoder().encodeToString(player().getInventory().getItemInMainHand().serializeAsBytes());
    
    player().sendMessage(Component.text(base64)
                                  .clickEvent(ClickEvent.copyToClipboard(base64))
                                  .hoverEvent(HoverEvent.showText(Component.text("<copy>"))));
  }
}
