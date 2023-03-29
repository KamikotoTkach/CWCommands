package tkachgeek.commands.command.plugin;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import tkachgeek.commands.command.ArgumentSet;
import tkachgeek.commands.command.Command;
import tkachgeek.commands.command.DebugMode;
import tkachgeek.commands.command.arguments.*;
import tkachgeek.commands.command.arguments.basic.BooleanArg;
import tkachgeek.commands.command.arguments.basic.DoubleArg;
import tkachgeek.commands.command.arguments.basic.IntegerArg;
import tkachgeek.commands.command.arguments.basic.StringArg;
import tkachgeek.commands.command.arguments.bukkit.*;
import tkachgeek.commands.command.arguments.bukkit.location.LocationPart;
import tkachgeek.commands.command.arguments.bukkit.location.TargetXArg;
import tkachgeek.commands.command.arguments.datetime.DurationArg;
import tkachgeek.commands.command.arguments.datetime.TimeArg;
import tkachgeek.commands.command.arguments.spaced.SafetySpacedStringArg;
import tkachgeek.commands.command.arguments.spaced.SpacedListArg;
import tkachgeek.commands.command.arguments.spaced.SpacedStringArg;
import tkachgeek.commands.command.permissions.PermissionGenerationStrategy;

import java.util.Arrays;

public class main extends JavaPlugin {
  @Override
  public void onEnable() {
    
    new Command("commandsTest", "*")
       .subCommands(
          new Command("bukkit")
             .arguments(
                new ArgumentSet(new PrintArguments(), new ExactStringArg("block"), new BlockArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("enchantment"), new EnchantmentArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("legacyColor"), new LegacyColorArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("material"), new MaterialArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("onlinePlayers"), new OnlinePlayers()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("particle"), new ParticleArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("player"), new PlayerArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("effect"), new PotionEffectArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("sound"), new SoundArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("world"), new WorldArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("onlinePlayersWithPermission"), new OnlinePlayerWithPermissionArg("*","admin")),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("nearPlayers"), new NearPlayersArg(10, 10))
                )
             .subCommands(
                new Command("target")
                   .arguments(
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("XYZ"), ComplexArg.xyz),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("X"), new TargetXArg(LocationPart.X)),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("Y"), new TargetXArg(LocationPart.Y)),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("Z"), new TargetXArg(LocationPart.Z)),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("PITCH"), new TargetXArg(LocationPart.PITCH)),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("YAW"), new TargetXArg(LocationPart.YAW)),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("WORLD"), new TargetXArg(LocationPart.WORLD))
                   )
             ),
          
          new Command("basic")
             .subCommands(
                new Command("string")
                   .subCommands(
                      new Command("spaced")
                         .arguments(
                            new ArgumentSet(new PrintArguments(), new ExactStringArg("simple"), new SpacedStringArg()),
                            new ArgumentSet(new PrintArguments(), new ExactStringArg("list"), new SpacedListArg("list", Arrays.asList("one two", "three four"))),
                            new ArgumentSet(new PrintArguments(), new ExactStringArg("safeString"), new SafetySpacedStringArg())
                         )
                   )
                   .arguments(
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("simple"), new StringArg()),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("list"), new ListArg("list", Arrays.asList("one", "two"))),
                      new ArgumentSet(new PrintArguments(), new ExactStringArg("safeString"), new SafetyStringArg())
                   )
             ).arguments(
                new ArgumentSet(new PrintArguments(), new ExactStringArg("int"), new IntegerArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("double"), new DoubleArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("boolean"), new BooleanArg())
             ),
          
          new Command("datetime")
             .arguments(
                new ArgumentSet(new PrintArguments(), new ExactStringArg("duration"), new DurationArg()),
                new ArgumentSet(new PrintArguments(), new ExactStringArg("time"), new TimeArg())
             )
       )
       .arguments(
          new ArgumentSet(new PrintArguments(), new ExactStringArg("empty"), new EmptyArg()),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("enum"), new EnumArg(PlayerQuitEvent.QuitReason.values(),"quitReasons")),
          new ArgumentSet(new PrintArguments(), new ExactStringArg("hexColor"), new HexColorArg("color"))
       )
       .setColorScheme(NamedTextColor.GREEN)
       .setPermissions(PermissionGenerationStrategy.ALL_DENIED)
       .register(this);
  }
}
