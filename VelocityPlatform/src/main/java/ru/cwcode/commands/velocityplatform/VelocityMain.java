package ru.cwcode.commands.velocityplatform;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;
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
import ru.cwcode.commands.permissions.PermissionGenerationStrategy;
import ru.cwcode.commands.velocityplatform.argument.OnlinePlayerWithPermissionArg;
import ru.cwcode.commands.velocityplatform.velocity.VelocityPlatform;

import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Plugin(
   id = "cwcommands",
   name = "CWCommands",
   version = "1.1.2",
   description = "A Pretty Commands Lib",
   url = "https://cwcode.ru/vk",
   authors = {"TkachGeek", "Soul_KRT"}
)

public class VelocityMain {
  @Inject
  private ProxyServer server;
  @Inject
  private Logger logger;
  @Inject
  @DataDirectory
  private Path dataFolder;
  
  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    VelocityPlatform platform = new VelocityPlatform(this, this.server, this.logger);
    CommandsAPI.setPlatform(platform);
    
    try {
      new Command("commandsTestv", "*")
         .subCommands(
            new Command("velocity")
               .arguments(
                  new ArgumentSet(new PrintArguments(), new ExactStringArg("onlinePlayersWithPermission"), new OnlinePlayerWithPermissionArg("*", "admin"))
               
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
            new ArgumentSet(new PrintArguments(), new ExactStringArg("enum"), new EnumArg(ChronoUnit.values(), "chronoUnits")),
            new ArgumentSet(new PrintArguments(), new ExactStringArg("hexColor"), new HexColorArg("color"))
         )
         .setColorScheme(NamedTextColor.GREEN)
         .setPermissions(PermissionGenerationStrategy.ALL_DENIED)
         .register();
      
    } catch (Exception ex) {
      this.logger.warn(ex.getLocalizedMessage());
    }
  }
  
  public static VelocityPlatform getPlatform() {
    return ((VelocityPlatform) CommandsAPI.getPlatform());
  }
}
