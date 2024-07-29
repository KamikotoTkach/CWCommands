package ru.cwcode.commands.extra;

import ru.cwcode.commands.Argument;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.extra.command.argument.KeyArgument;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RepositoryAccessor<E, K, S extends Sender> {
  private final BiFunction<E, S, K> elementToKey;
  private final BiFunction<K, S, E> keyToElement;
  private final Function<K, String> keyToString;
  private final Function<String, K> stringToKey;
  private final Function<S, Collection<K>> keys;
  
  public RepositoryAccessor(BiFunction<E, S, K> elementToKey,
                            BiFunction<K, S, E> keyToElement,
                            Function<K, String> keyToString,
                            Function<String, K> stringToKey,
                            Function<S, Collection<K>> keys) {
    this.elementToKey = elementToKey;
    this.keyToElement = keyToElement;
    this.keyToString = keyToString;
    this.stringToKey = stringToKey;
    this.keys = keys;
  }
  
  public Collection<K> keys(S sender) {
    return keys.apply(sender);
  }
  
  public K elementToKey(E element, S sender) {
    return elementToKey.apply(element, sender);
  }
  
  public E keyToElement(S sender, K key) {
    return keyToElement.apply(key, sender);
  }
  
  public String keyToString(K key) {
    return keyToString.apply(key);
  }
  
  public K stringToKey(String string) {
    return stringToKey.apply(string);
  }
  
  public Argument keyArgument() {
    return new KeyArgument<E, K, S>(this); //мб стоит 1 сохранить
  }
}