package ru.cwcode.commands.color;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HsvColorsTest {
  @Test
  void matchesAdventureConversion() {
    for (int h = 0; h < 100; h++) {
      for (int s = 0; s <= 10; s++) {
        for (int v = 0; v <= 10; v++) {
          float hue = h / 100f;
          float saturation = s / 10f;
          float value = v / 10f;

          // HSVLike.of is the 4.7 factory (Paper 1.16.5); the test classpath has that Adventure on purpose
          TextColor expected = TextColor.color(HSVLike.of(hue, saturation, value));

          assertEquals(expected, HsvColors.color(hue, saturation, value), "h=" + hue + " s=" + saturation + " v=" + value);
        }
      }
    }
  }

  @Test
  void clampsOutOfRangeSaturationAndValue() {
    assertEquals(HsvColors.color(0.3f, 1f, 1f), HsvColors.color(0.3f, 1.4f, 1.2f));
    assertEquals(HsvColors.color(0.3f, 0f, 0f), HsvColors.color(0.3f, -0.2f, -1f));
  }

  @Test
  void hueWrapsAround() {
    assertEquals(HsvColors.color(0.25f, 0.7f, 0.8f), HsvColors.color(1.25f, 0.7f, 0.8f));
  }
}
