package probe;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.BrigadierConformance;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.paperplatform.paper.PaperPlatform;
import ru.cwcode.commands.paperplatform.paper.PaperSender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.arguments.basic.IntegerArg;
import ru.cwcode.commands.paperplatform.PrintArguments;
import ru.cwcode.commands.paperplatform.argument.BlockArg;
import ru.cwcode.commands.paperplatform.argument.EnchantmentArg;
import ru.cwcode.commands.paperplatform.argument.MaterialArg;
import ru.cwcode.commands.paperplatform.argument.ParticleArg;
import ru.cwcode.commands.paperplatform.argument.PotionEffectArg;
import ru.cwcode.commands.paperplatform.argument.SoundArg;
import ru.cwcode.commands.paperplatform.argument.WorldArg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Acceptance probe for the Commands library: exercises the Bukkit-registry-backed arguments and a real command
 * dispatch. Every line starting with PROBE is a result; PROBE FAIL means the platform breaks the library.
 */
public class CommandsProbe extends JavaPlugin {
  private int failures;
  
  @Override
  public void onEnable() {
    getLogger().info("PROBE platform " + Bukkit.getVersion());
    
    check("material", new MaterialArg(), "STONE", "NOT_A_MATERIAL");
    check("block", new BlockArg(), "STONE", "NOT_A_BLOCK");
    check("enchantment", new EnchantmentArg(), "DAMAGE_ALL", "NOT_AN_ENCHANT");
    check("particle", new ParticleArg(), "FLAME", "NOT_A_PARTICLE");
    check("potionEffect", new PotionEffectArg(), "SPEED", "NOT_AN_EFFECT");
    check("sound", new SoundArg(), "ENTITY_ARROW_HIT", "NOT_A_SOUND");
    check("world", new WorldArg(), Bukkit.getWorlds().get(0).getName(), "no_such_world");
    
    dispatch();
    registerAfterStartupChecks();

    getLogger().info("PROBE " + (failures == 0 ? "OK" : "FAIL count=" + failures));
  }

  /**
   * On Paper 1.20.6+ commands reach Brigadier only in LifecycleEvents.COMMANDS, which fires after all plugins
   * enable — so these commands are dispatched on ServerLoadEvent, not in onEnable.
   */
  private void registerAfterStartupChecks() {
    new Command("cmdprobeyml")
       .arguments(new ArgumentSet(new ProbeExecutor("yml"), new IntegerArg()))
       .register();
    new Command("cmdprobefree")
       .aliases("cpfree")
       .arguments(new ArgumentSet(new ProbeExecutor("free"), new IntegerArg()))
       .register();

    getServer().getPluginManager().registerEvents(new Listener() {
      @EventHandler
      public void onLoad(ServerLoadEvent event) {
        int startupFailures = 0;
        startupFailures += expect("cmdprobeyml 1", "yml:1");
        startupFailures += expect("cmdprobefree 2", "free:2");
        startupFailures += expect("cpfree 3", "free:3");
        startupFailures += expect("commandsprobe:cmdprobefree 7", "free:7");
        if (brigadierPath()) {
          // до 1.20.6 команда идёт через Bukkit: двойной пробел даёт пустое слово, execute run её не видит
          startupFailures += expect("cmdprobefree  6", "free:6");
          startupFailures += expect("execute run cmdprobefree 8", "free:8");
        }
        startupFailures += expectNoCall("cmdprobefree x:y");
        startupFailures += expectNoCall("cmdprobefree 9 extra");
        startupFailures += conformance();
        getLogger().info("PROBE registration in plugin.yml: cmdprobeyml=" + (getCommand("cmdprobeyml") != null)
                         + " cmdprobefree=" + (getCommand("cmdprobefree") != null));
        getLogger().info("PROBE STARTUP " + (startupFailures == 0 ? "OK" : "FAIL count=" + startupFailures));
      }
    }, this);
  }

  private int expect(String commandLine, String expectedCall) {
    try {
      ProbeExecutor.calls.clear();
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
      boolean ok = ProbeExecutor.calls.contains(expectedCall);
      getLogger().info("PROBE " + (ok ? "ok  " : "FAIL") + " /" + commandLine + " -> " + ProbeExecutor.calls);
      return ok ? 0 : 1;
    } catch (Throwable t) {
      getLogger().severe("PROBE FAIL /" + commandLine + ": " + t);
      t.printStackTrace();
      return 1;
    }
  }
  
  private static boolean brigadierPath() {
    try {
      Class.forName("io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  /**
   * Input our parser rejects must reach it (our error, no call) instead of failing in Brigadier.
   */
  private int expectNoCall(String commandLine) {
    try {
      ProbeExecutor.calls.clear();
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
      boolean ok = ProbeExecutor.calls.isEmpty();
      getLogger().info("PROBE " + (ok ? "ok  " : "FAIL") + " /" + commandLine + " -> " + ProbeExecutor.calls);
      return ok ? 0 : 1;
    } catch (Throwable t) {
      getLogger().severe("PROBE FAIL /" + commandLine + ": " + t);
      return 1;
    }
  }

  /**
   * Every CWCommands command on this server through a real Brigadier dispatcher (BrigadierConformance).
   */
  private int conformance() {
    try {
      int errors = 0;
      for (Command command : ((PaperPlatform) CommandsAPI.getPlatform()).registeredCommands()) {
        BrigadierConformance.Report report = BrigadierConformance.check(command, new PaperSender(Bukkit.getConsoleSender()));
        errors += report.errors().size();
        getLogger().info("PROBE brigadier " + report);
        report.errors().forEach(error -> getLogger().warning("PROBE brigadier error " + error));
      }
      getLogger().info("PROBE BRIGADIER " + (errors == 0 ? "OK" : "FAIL count=" + errors));
      return errors == 0 ? 0 : 1;
    } catch (Throwable t) {
      getLogger().severe("PROBE FAIL brigadier conformance: " + t);
      t.printStackTrace();
      return 1;
    }
  }

  private void check(String name, Argument argument, String known, String unknown) {
    try {
      Collection<String> completions = argument.completions(null);
      boolean validKnown = argument.valid(known);
      boolean validUnknown = argument.valid(unknown);
      List<String> sample = new ArrayList<>(completions).stream().sorted().limit(3).collect(Collectors.toList());
      boolean ok = !completions.isEmpty() && validKnown && !validUnknown;
      getLogger().info("PROBE " + (ok ? "ok  " : "FAIL") + " " + name + " completions=" + completions.size()
                       + " sample=" + sample + " valid(" + known + ")=" + validKnown + " valid(" + unknown + ")=" + validUnknown);
      if (!ok) failures++;
    } catch (Throwable t) {
      failures++;
      getLogger().severe("PROBE FAIL " + name + ": " + t);
      t.printStackTrace();
    }
  }
  
  private void dispatch() {
    try {
      new Command("cmdprobe", "*")
         .arguments(new ArgumentSet(new PrintArguments(), new ExactStringArg("int"), new IntegerArg()))
         .register();
      boolean ran = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmdprobe int 42");
      getLogger().info("PROBE " + (ran ? "ok  " : "FAIL") + " dispatch cmdprobe int 42 -> " + ran);
      if (!ran) failures++;
    } catch (Throwable t) {
      failures++;
      getLogger().severe("PROBE FAIL dispatch: " + t);
      t.printStackTrace();
    }
  }
}
