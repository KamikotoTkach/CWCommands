package ru.cwcode.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.color.ColorGenerationStrategy;
import ru.cwcode.cwutils.messages.Message;
import ru.cwcode.cwutils.messages.MessageReturn;

import java.util.Collection;
import java.util.List;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public abstract class Argument {
   protected String raw;
   private String tag = "";
   private boolean optional;

   protected Argument(String raw) {
      this.raw = raw;
   }

   public Argument() {
   }

   /**
    * Оформление аргумента в авто-хелпе
    */
   @NotNull
   public TextComponent toComponent(ColorGenerationStrategy color, boolean canPerformedBy) {
      if (isOptional()) {
         return Component.text("[" + argumentName() + "]", color.optional(canPerformedBy));
      } else if (this instanceof ExactStringArg) {
         return Component.text(argumentName(), color.subcommand(canPerformedBy));
      } else {
         TextComponent component = Component.text("<" + argumentName() + ">", color.argument(canPerformedBy));
         if (hint().isEmpty()) {
            return component;
         }
         return component.hoverEvent(HoverEvent.showText(Component.text(hint(), color.accent(canPerformedBy))));
      }
   }

   public String toReadableString() {
      if (isOptional()) {
         return "[" + argumentName() + "]";
      } else if (this instanceof ExactStringArg) {
         return argumentName();
      } else {
         var args = "<" + argumentName() + ">";
         if (hint().isEmpty()) {
            return args;
         }
         return args;
      }
   }

   public abstract boolean valid(String raw);

   public boolean valid(Sender sender, String raw, List<String> arguments) {
      return valid(raw);
   }

   public abstract Collection<String> completions(Sender sender);

   public Collection<String> completions(Sender sender, List<String> written) {
      return completions(sender);
   }

   /**
    * Делает аргумент опциональным. Должен быть в конце, если таких несколько - все в конце
    */
   public Argument optional() {
      optional = true;
      return this;
   }

   /**
    * Делает аргумент опциональным. Должен быть в конце, если таких несколько - все в конце
    */
   public Argument optional(String defaultValue) {
      optional = true;
      raw = defaultValue;
      return this;
   }

   /**
    * Название аргумента в хелпе
    */
   public abstract String argumentName();

   public boolean notNull() {
      return raw != null;
   }

   public Integer toInt() {
      try {
         if (notNull()) return Integer.parseInt(raw);
      } catch (Exception exception) {
         CommandsAPI.getPlatform().getLogger().warn(l10n.get("argument.cast.integer.error",raw, argumentName()));
      }
      return null;
   }

   public Float toFloat() {
      try {
         if (notNull()) return Float.parseFloat(raw);
      } catch (Exception exception) {
         CommandsAPI.getPlatform().getLogger().warn(l10n.get("argument.cast.float.error",raw, argumentName()));
      }

      return null;
   }

   public String toString() {
      if (notNull()) return raw;
      CommandsAPI.getPlatform().getLogger().warn(l10n.get("argument.cast.string.error", (raw != null ? raw : "~null~"), argumentName()));
      return null;
   }

   public Double toDouble() {
      try {
         if (notNull()) return Double.parseDouble(raw);
      } catch (Exception exception) {
         CommandsAPI.getPlatform().getLogger().warn(l10n.get("argument.cast.double.error",raw, argumentName()));
      }
      return null;
   }

   public Boolean toBoolean() {
      try {
         if (notNull()) return Boolean.parseBoolean(raw);
      } catch (Exception exception) {
         CommandsAPI.getPlatform().getLogger().warn(l10n.get("argument.cast.boolean.error",raw, argumentName()));
      }

      return null;
   }

   public boolean isOptional() {
      return optional;
   }

   /**
    * Подсказка при наведении на аргумент в авто-хелпе
    */
   protected String hint() {
      return "";
   }


   public Argument newInstance() throws MessageReturn {
      try {
         return this.getClass().getDeclaredConstructor().newInstance();
      } catch (Exception ignored) {
         throw new MessageReturn(
               Message.from(l10n.get("argument.newInstance.error", argumentName()))
         );
      }
   }

   public Argument tag(String tag) {
      this.tag = tag;
      return this;
   }

   public String getTag() {
      return tag.isEmpty() ? argumentName() : tag;
   }

   public Component invalidMessage(Command command, Sender sender, String written) {
      ColorGenerationStrategy colorScheme = command.getColorScheme();

      return Component.text(written, colorScheme.accent(true))
                      .append(Component.text(l10n.get("argument.invalid.error"), colorScheme.main()))
                      .append(Component.text(argumentName(), colorScheme.accent(true)));
   }
   
   @ApiStatus.OverrideOnly
   public Object map() {
      return raw;
   }

}
