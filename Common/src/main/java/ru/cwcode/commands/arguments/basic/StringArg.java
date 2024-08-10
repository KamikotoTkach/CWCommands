package ru.cwcode.commands.arguments.basic;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.CompletionStyle;
import ru.cwcode.commands.api.Sender;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static ru.cwcode.commands.api.CommandsAPI.l10n;

public class StringArg extends Argument {
   static final int INITIAL_MAX_STRING_LENGTH = 10000;
   protected String placeholder =  l10n.get("argument.string.placeholder");
   int min = 1;
   int max = INITIAL_MAX_STRING_LENGTH;
   CompletionStyle style = CompletionStyle.PLACEHOLDER;
   Pattern pattern;

   public StringArg(String placeholder) {
      this.placeholder = placeholder;
   }

   public StringArg() {
   }

   public StringArg setMin(int min) {
      this.min = min;
      return this;
   }

   public StringArg setMax(int max) {
      this.max = max;
      return this;
   }

   public StringArg setStyle(CompletionStyle style) {
      this.style = style;
      return this;
   }

   public StringArg setPlaceholder(String placeholder) {
      this.placeholder = placeholder;
      return this;
   }

   public StringArg setPattern(Pattern pattern) {
      this.pattern = pattern;
      return this;
   }

   @Override
   public boolean valid(String raw) {
      if (raw.length() < min || raw.length() > max) return false;
      return pattern == null || pattern.matcher(raw).matches();
   }

   @Override
   public List<String> completions(Sender sender) {
     if (Objects.requireNonNull(style) == CompletionStyle.PLACEHOLDER) {
       return Collections.singletonList(placeholder);
     }
     return Collections.emptyList();
   }

   @Override
   public String argumentName() {
      return placeholder;
   }

   @Override
   protected String hint() {
      if (pattern != null) return pattern.pattern();
      
      boolean maxFlag = max != INITIAL_MAX_STRING_LENGTH;
      
      if (maxFlag) return l10n.get("argument.string.minmax", min, max);
      return l10n.get("argument.string.min", min);
   }
}