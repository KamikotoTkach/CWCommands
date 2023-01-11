package tkachgeek.commands.command.arguments.bukkit;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SoundArg extends Argument {
    static List<String> sounds = Arrays.stream(Sound.values()).map(Enum::name).collect(Collectors.toList());

    @Override
    public boolean valid(String raw) {
        return sounds.contains(raw);
    }

    @Override
    public List<String> completions(CommandSender sender) {
        return sounds;
    }

    @Override
    public String argumentName() {
        return "материал";
    }
}
