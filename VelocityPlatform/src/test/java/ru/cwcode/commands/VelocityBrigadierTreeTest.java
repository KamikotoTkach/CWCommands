package ru.cwcode.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.RootCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import org.junit.jupiter.api.Test;
import ru.cwcode.commands.CommandRuntimeTest.Deny;
import ru.cwcode.commands.CommandRuntimeTest.TestSender;
import ru.cwcode.commands.velocityplatform.velocity.ClientCommandTrees;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Все проверки {@link BrigadierTreeTest} на форке brigadier из Velocity: Common собран против
 * {@code com.mojang:brigadier}, а на прокси работает {@code com.velocitypowered:velocity-brigadier}.
 * Плюс подмена дерева для клиента ({@link ClientCommandTrees}).
 */
class VelocityBrigadierTreeTest extends BrigadierTreeTest {

  @Test
  void runsOnTheVelocityFork() {
    String location = CommandDispatcher.class.getProtectionDomain().getCodeSource().getLocation().toString();
    assertTrue(location.contains("velocity-brigadier"), location);
  }

  @Test
  void clientGetsTheFullTreeWithoutHiddenBranches() {
    Command rich = rich();
    Command denied = new Command("denied", new CommandRuntimeTest.Recorder.NoArgs()).preconditions(new Deny());
    denied.register();

    BrigadierTree<CommandSource> tree = new BrigadierTree<>(source -> new TestSender(((Source) source).name),
                                                            (source, args) -> {}, (source, args) -> List.of(), source -> true);
    ClientCommandTrees trees = new ClientCommandTrees();
    trees.add(List.of("rich", "r", "absent"), tree.build(rich, "rich", tree.rootVisibility(rich)));
    trees.add(List.of("denied"), tree.build(denied, "denied", tree.rootVisibility(denied)));

    RootCommandNode<CommandSource> root = new RootCommandNode<>();
    for (String name : List.of("rich", "r", "denied", "other")) {
      root.addChild(LiteralArgumentBuilder.<CommandSource>literal(name).executes(context -> 1).build());
    }

    trees.inject(root, new Source("alice"));

    assertNotNull(root.getChild("rich").getChild("list"));
    assertNotNull(root.getChild("rich").getChild("give").getChild("player"));
    assertNull(root.getChild("rich").getChild("hidden"));
    assertNull(root.getChild("rich").getChild("secret"));
    assertNotNull(root.getChild("r").getChild("give"), "aliases get the tree too");
    assertNull(root.getChild("absent"), "a name Velocity did not announce stays absent");
    assertNull(root.getChild("denied"), "an invisible command leaves the client tree");
    assertNotNull(root.getChild("other"));
  }

  record Source(String name) implements CommandSource {
    @Override
    public Tristate getPermissionValue(String permission) {
      return Tristate.TRUE;
    }
  }
}
