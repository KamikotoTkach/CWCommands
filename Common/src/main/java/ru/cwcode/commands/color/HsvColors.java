package ru.cwcode.commands.color;

import net.kyori.adventure.text.format.TextColor;

/**
 * HSV to RGB without {@code HSVLike.hsvLike}/{@code HSVLike.of}: the factory is called {@code of} up to Adventure 4.11 and
 * {@code hsvLike} from 4.12 (5.x dropped {@code of}), and Paper 1.16.5 ships 4.7.0, so no single call works everywhere.
 */
final class HsvColors {
  private HsvColors() {
  }
  
  /**
   * @param h hue in [0, 1], wraps around
   * @param s saturation, clamped to [0, 1]
   * @param v value, clamped to [0, 1]
   */
  static TextColor color(float h, float s, float v) {
    s = Math.max(0f, Math.min(1f, s));
    v = Math.max(0f, Math.min(1f, v));
    h = h - (float) Math.floor(h);
    
    float sector = h * 6f;
    int i = (int) Math.floor(sector);
    float f = sector - i;
    float p = v * (1f - s);
    float q = v * (1f - f * s);
    float t = v * (1f - (1f - f) * s);
    
    float r;
    float g;
    float b;
    switch (i % 6) {
      case 0:
        r = v; g = t; b = p;
        break;
      case 1:
        r = q; g = v; b = p;
        break;
      case 2:
        r = p; g = v; b = t;
        break;
      case 3:
        r = p; g = q; b = v;
        break;
      case 4:
        r = t; g = p; b = v;
        break;
      default:
        r = v; g = p; b = q;
        break;
    }
    
    return TextColor.color((int) (r * 255f) << 16 | (int) (g * 255f) << 8 | (int) (b * 255f));
  }
}
