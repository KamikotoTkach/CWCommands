package probe;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.commands.Argument;
import ru.cwcode.commands.ArgumentSet;
import ru.cwcode.commands.Command;
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
    
    getLogger().info("PROBE " + (failures == 0 ? "OK" : "FAIL count=" + failures));
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
