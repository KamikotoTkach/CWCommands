package tkachgeek.commands.command.arguments.bukkit;

import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleArg extends Argument {
    static List<String> particles = Arrays.stream(Particle.values()).map(Enum::name).collect(Collectors.toList());

    @Override
    public boolean valid(String raw) {
        return particles.contains(raw);
    }

    @Override
    public List<String> completions(CommandSender sender) {
        return particles;
    }

    @Override
    public String argumentName() {
        return "материал";
    }
}
