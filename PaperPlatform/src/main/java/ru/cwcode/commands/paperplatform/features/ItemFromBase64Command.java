package ru.cwcode.commands.paperplatform.features;

import org.bukkit.inventory.ItemStack;
import ru.cwcode.commands.paperplatform.executor.Executor;
import tkachgeek.tkachutils.messages.MessageReturn;
import tkachgeek.tkachutils.messages.TargetableMessageReturn;

import java.util.Base64;

public class ItemFromBase64Command extends Executor {
  @Override
  public void executeForPlayer() throws MessageReturn, TargetableMessageReturn {
    String base64 = argS(1);
    
    ItemStack itemStack = ItemStack.deserializeBytes(Base64.getDecoder().decode(base64));
    
    player().getInventory().addItem(itemStack);
  }
}
