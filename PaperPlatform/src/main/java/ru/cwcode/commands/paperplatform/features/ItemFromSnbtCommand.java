package ru.cwcode.commands.paperplatform.features;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import ru.cwcode.commands.paperplatform.executor.Executor;
import ru.cwcode.cwutils.items.ItemStackUtils;
import ru.cwcode.cwutils.messages.MessageReturn;
import ru.cwcode.cwutils.messages.TargetableMessageReturn;

public class ItemFromSnbtCommand extends Executor {
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    String snbt = argS(1);
    
    ItemStack itemStack = ItemStackUtils.fromSNBT(snbt);
    if (itemStack == null) {
      player().sendMessage(Component.text("Invalid snbt item", NamedTextColor.RED));
    } else {
      player().getInventory().addItem(itemStack);
    }
  }
}
