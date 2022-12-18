package tkachgeek.commands.command.arguments;

import org.bukkit.command.CommandSender;
import tkachgeek.commands.command.Argument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArg extends Argument {
    private final List<String> enum_values = new ArrayList<>();

    public EnumArg(Enum[] instance) {
        this.enum_values.addAll(Stream.of(instance)
                .map(Enum::name).collect(Collectors.toList()));
    }

    @Override
    public boolean valid(String s) {
        return enum_values.contains(s);
    }

    @Override
    public List<String> completions(CommandSender commandSender) {
        return enum_values;
    }

    @Override
    public String argumentName() {
        return "Список значений";
    }
}
