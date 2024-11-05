package ru.cwcode.commands.paperplatform.brigadier;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.cwcode.commands.paperplatform.paper.PaperPlatform;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class BrigadierListener implements Listener {
  final PaperPlatform paperPlatform;
  // CommandNode#children, CommandNode#literals, CommandNode#arguments fields
  protected static final Field CHILDREN_FIELD;
  protected static final Field LITERALS_FIELD;
  protected static final Field ARGUMENTS_FIELD;
  
  // An array of the CommandNode fields above: [#children, #literals, #arguments]
  protected static final Field[] CHILDREN_FIELDS;
  static {
    try {
      CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
      LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
      ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
      CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD};
      for (Field field : CHILDREN_FIELDS) {
        field.setAccessible(true);
      }
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  
  public BrigadierListener(PaperPlatform paperPlatform) {
    this.paperPlatform = paperPlatform;
    
  }
  
  @EventHandler
  public void onCommand(AsyncPlayerSendCommandsEvent<?> event) {
    if (event.isAsynchronous() || !event.hasFiredAsync()) {
      paperPlatform.registeredCommands().forEach((alias, command) -> {
        
        removeChild(event.getCommandNode(), alias);
        event.getCommandNode().addChild(new LiteralCommandNode<>(alias,
                                                                  context -> 1,
                                                                  object -> true,
                                                                  null,
                                                                  context -> Collections.emptyList(),
                                                                  false));
      });
    }
  }
  
  protected static void removeChild(RootCommandNode root, String name) {
    try {
      for (Field field : CHILDREN_FIELDS) {
        Map<String, ?> children = (Map<String, ?>) field.get(root);
        children.remove(name);
      }
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
