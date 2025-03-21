package ru.cwcode.commands.paperplatform;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.arguments.*;
import ru.cwcode.commands.arguments.basic.BooleanArg;
import ru.cwcode.commands.arguments.basic.DoubleArg;
import ru.cwcode.commands.arguments.basic.IntegerArg;
import ru.cwcode.commands.arguments.basic.StringArg;
import ru.cwcode.commands.arguments.datetime.DurationArg;
import ru.cwcode.commands.arguments.datetime.TimeArg;
import ru.cwcode.commands.arguments.spaced.SafetySpacedStringArg;
import ru.cwcode.commands.arguments.spaced.SpacedListArg;
import ru.cwcode.commands.arguments.spaced.SpacedStringArg;
import ru.cwcode.commands.color.ColoredScheme;
import ru.cwcode.commands.paperplatform.argument.*;
import ru.cwcode.commands.paperplatform.argument.location.LocationArg;
import ru.cwcode.commands.paperplatform.argument.location.LocationPart;
import ru.cwcode.commands.paperplatform.argument.location.TargetXArg;
import ru.cwcode.commands.paperplatform.brigadier.BrigadierListener;
import ru.cwcode.commands.paperplatform.features.*;
import ru.cwcode.commands.paperplatform.paper.PaperPlatform;
import ru.cwcode.commands.paperplatform.paper.PaperSender;
import ru.cwcode.commands.permissions.PermissionGenerationStrategy;
import ru.cwcode.commands.preconditions.impl.PredicatePrecondition;
import ru.cwcode.cwutils.collections.CollectionUtils;
import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.cwutils.l10n.L10n;
import ru.cwcode.cwutils.l10n.PaperL10nPlatform;
import ru.cwcode.cwutils.server.ServerUtils;

import java.util.Arrays;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public final class PaperMain extends JavaPlugin {
  
  public static JavaPlugin plugin;
  
  @Override
  public void onLoad() {
    PaperL10nPlatform l10nPlatform = new PaperL10nPlatform(this, this.getFile());
    
    CommandsAPI.setL10n(new L10n(l10nPlatform));
    CommandsAPI.setConfig(new SimpleConfig("config", l10nPlatform));
  }
  
  @Override
  public void onEnable() {
    plugin = this;
    
    PaperPlatform platform = new PaperPlatform();
    
    CommandsAPI.setPlatform(platform);
    
    registerBrigadierHook(platform);
    
    sendLogo();
    
    if (ServerUtils.isVersionBeforeOrEqual1_12_2()) return;
    
    try {
      new Command("commandsTest", "*")
         .subCommands(
            new Command("feature")
               .arguments(
                  new ArgumentSet(new ItemToSnbtCommand(), new ExactStringArg("mainItemToSnbt")),
                  new ArgumentSet(new ItemToBase64Command(), new ExactStringArg("mainItemToBase64")),
                  new ArgumentSet(new ItemToGsonCommand(), new ExactStringArg("mainItemToGson")),
                  
                  new ArgumentSet(new ItemFromSnbtCommand(), new ExactStringArg("itemFromSnbt"), new SpacedStringArg("snbt")),
                  new ArgumentSet(new ItemFromBase64Command(), new ExactStringArg("itemFromBase64"), new StringArg("base64"))
               ),
            new Command("preconditionTest")
               .arguments(
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("onlyIfFlying"))
                     .canExecute(sender -> ((PaperSender) sender).getPlayer().isFlying()),
                  
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("onlyIfNotFlying"))
                     .preconditions(
                        new PredicatePrecondition(sender -> !((PaperSender) sender).getPlayer().isFlying(), "Игрок должен стоять на земле")
                     ),
                  
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("onlyForPlayer"))
                     .blockForNonPlayers(),
                  
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("onlyForNonPlayer"))
                     .blockForPlayers()
               ),
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
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("onlinePlayersWithPermission"), new OnlinePlayerWithPermissionArg("*", "admin")),
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("nearPlayers"), new NearPlayersArg(10, 10))
               )
               .subCommands(
                  new Command("target")
                     .arguments(
                        new ArgumentSet(new PrintArguments(), new ExactStringArg("XYZ"), LocationArg.xyz),
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
                     ),
                  new Command("auto")
                     .arguments(
                        new ArgumentSet(new PrintArguments(), new IntegerArg()),
                        new ArgumentSet(new PrintArguments(), new DoubleArg()),
                        new ArgumentSet(new PrintArguments(), new BooleanArg())
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
            new ArgumentSet(new PrintArguments(), new ExactStringArg("enum"), new EnumArg(EquipmentSlot.values(), "equipmentSlot")),
            new ArgumentSet(new PrintArguments(), new ExactStringArg("hexColor"), new HexColorArg("color"))
         )
         .setColorScheme(new ColoredScheme(NamedTextColor.GREEN, NamedTextColor.RED))
         .setPermissions(PermissionGenerationStrategy.ALL_DENIED)
         .register();
    } catch (Exception ex) {
      this.getLogger().warning(ex.getLocalizedMessage());
    }
  }
  
  private void registerBrigadierHook(PaperPlatform platform) {
    try {
      Class.forName("com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent");
      Class.forName("io.papermc.paper.command.brigadier.CommandSourceStack");
      registerBrigadierHook0(platform);
    } catch (ClassNotFoundException e) {
    }
  }
  
  private void registerBrigadierHook0(PaperPlatform platform) {
    Bukkit.getPluginManager().registerEvents(new BrigadierListener(platform),this);
  }
  
  private void sendLogo() {
    NamedTextColor randomColor = CollectionUtils.getRandomListEntry(List.of(NamedTextColor.DARK_GREEN, NamedTextColor.DARK_RED, NamedTextColor.DARK_BLUE, NamedTextColor.BLUE, NamedTextColor.YELLOW, NamedTextColor.GOLD, NamedTextColor.GRAY));
    
    ConsoleCommandSender console = Bukkit.getConsoleSender();
    
    console.sendMessage("");
    console.sendMessage(Component.text("  ___ _         _  __      __       _   ", randomColor));
    console.sendMessage(Component.text(" / __| |___  __| |_\\ \\    / /__ _ _| |__", randomColor));
    console.sendMessage(Component.text("| (__| / _ \\/ _| / /\\ \\/\\/ / _ \\ '_| / /", randomColor));
    console.sendMessage(Component.text(" \\___|_\\___/\\__|_\\_\\ \\_/\\_/\\___/_| |_\\_\\", randomColor));
    console.sendMessage(Component.text(l10n.get("startup.logo.tooltip"), randomColor));
    console.sendMessage("");
  }
}
