package ru.cwcode.commands.paperplatform.features;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ru.cwcode.commands.paperplatform.executor.Executor;
import ru.cwcode.cwutils.items.ItemStackUtils;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

public class ItemToSnbtCommand extends Executor {
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    String snbt = ItemStackUtils.toSNBT(player().getInventory().getItemInMainHand());
    
    player().sendMessage(Component.text(snbt)
                                  .clickEvent(ClickEvent.copyToClipboard(snbt))
                                  .hoverEvent(HoverEvent.showText(Component.text("<copy>"))));
    
  }
}
