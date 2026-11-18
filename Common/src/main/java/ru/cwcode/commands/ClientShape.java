package ru.cwcode.commands;

import java.util.Objects;

/**
 * Как аргумент выглядит в дереве команд клиента (Brigadier): тип узла для подсветки и подсказки
 * {@code <имя>}. Разбор и исполнение всё равно наши — значение, которое клиентский тип не принял,
 * уходит в «хвост» и проверяется {@link Argument#valid}. Поэтому форма влияет только на подсветку.
 * <p>
 * {@link #WORD} в Brigadier принимает лишь {@code [0-9A-Za-z_.+-]}: аргумент, чьи значения содержат
 * {@code :}, кириллицу или {@code #}, лучше объявить {@link #GREEDY} — тогда подсветка кончается на нём.
 */
public final class ClientShape {
  public static final ClientShape WORD = new ClientShape(Kind.WORD, 0, 0);
  public static final ClientShape BOOLEAN = new ClientShape(Kind.BOOLEAN, 0, 0);
  public static final ClientShape GREEDY = new ClientShape(Kind.GREEDY, 0, 0);

  private final Kind kind;
  private final double min;
  private final double max;

  private ClientShape(Kind kind, double min, double max) {
    this.kind = kind;
    this.min = min;
    this.max = max;
  }

  public static ClientShape integer(int min, int max) {
    return new ClientShape(Kind.INTEGER, min, max);
  }

  public static ClientShape decimal(double min, double max) {
    return new ClientShape(Kind.DOUBLE, min, max);
  }

  public Kind kind() {
    return kind;
  }

  public double min() {
    return min;
  }

  public double max() {
    return max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientShape)) return false;
    ClientShape that = (ClientShape) o;
    return kind == that.kind && min == that.min && max == that.max;
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, min, max);
  }

  @Override
  public String toString() {
    return kind == Kind.INTEGER || kind == Kind.DOUBLE ? kind + "[" + min + ".." + max + "]" : kind.toString();
  }

  public enum Kind {
    WORD, INTEGER, DOUBLE, BOOLEAN, GREEDY
  }
}
