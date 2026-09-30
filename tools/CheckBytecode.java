import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * R3 check (LIBS_MULTIPLATFORM.md): the bytecode level of a built jar.
 * <pre>
 * java CheckBytecode.java &lt;jar&gt; &lt;legacyMaxMajor&gt; &lt;v26Major&gt; &lt;packagePrefix[=major]&gt;...
 * </pre>
 * Every class outside the listed packages must have major &le; legacyMaxMajor; every class inside must have
 * exactly its major (v26Major unless the prefix says {@code package=major}, e.g. a Java 21 adapter next to the Java 25
 * ones). Each prefix must hold at least one class, otherwise the check would pass on a jar that lost its adapters.
 * Exit code 1 on any violation.
 */
public class CheckBytecode {
  public static void main(String[] args) throws IOException {
    if (args.length < 4) {
      System.err.println("usage: CheckBytecode <jar> <legacyMaxMajor> <v26Major> <packagePrefix[=major]>...");
      System.exit(2);
    }
    String jar = args[0];
    int legacyMax = Integer.parseInt(args[1]);
    int v26Major = Integer.parseInt(args[2]);
    List<String> prefixes = new ArrayList<>();
    List<Integer> prefixMajors = new ArrayList<>();
    for (int i = 3; i < args.length; i++) {
      String[] parts = args[i].split("=", 2);
      prefixes.add(parts[0].replace('.', '/'));
      prefixMajors.add(parts.length == 2 ? Integer.parseInt(parts[1]) : v26Major);
    }

    int[] v26Count = new int[prefixes.size()];
    int legacyCount = 0;
    int highestLegacy = 0;
    List<String> violations = new ArrayList<>();

    try (JarFile file = new JarFile(jar)) {
      Enumeration<? extends ZipEntry> entries = file.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        if (!name.endsWith(".class") || name.endsWith("module-info.class")) continue;

        int major = majorOf(file.getInputStream(entry), name);
        int prefixIndex = prefixIndex(prefixes, name);
        if (prefixIndex >= 0) {
          v26Count[prefixIndex]++;
          int expected = prefixMajors.get(prefixIndex);
          if (major != expected) violations.add(name + ": major " + major + ", expected " + expected);
        } else {
          legacyCount++;
          highestLegacy = Math.max(highestLegacy, major);
          if (major > legacyMax) violations.add(name + ": major " + major + " > " + legacyMax);
        }
      }
    }

    for (int i = 0; i < prefixes.size(); i++) {
      if (v26Count[i] == 0) violations.add("no classes under " + prefixes.get(i));
    }

    System.out.println("CheckBytecode " + jar);
    System.out.println("  legacy classes: " + legacyCount + ", highest major " + highestLegacy + " (limit " + legacyMax + ")");
    for (int i = 0; i < prefixes.size(); i++) {
      System.out.println("  " + prefixes.get(i) + ": " + v26Count[i] + " classes, major " + prefixMajors.get(i));
    }
    if (!violations.isEmpty()) {
      violations.stream().limit(50).forEach(v -> System.err.println("  VIOLATION " + v));
      System.err.println("  " + violations.size() + " violation(s)");
      System.exit(1);
    }
    System.out.println("  OK");
  }

  private static int prefixIndex(List<String> prefixes, String entryName) {
    // Multi-release copies (META-INF/versions/N/...) are judged by the path they shadow.
    String path = entryName.startsWith("META-INF/versions/")
                  ? entryName.substring(entryName.indexOf('/', "META-INF/versions/".length()) + 1)
                  : entryName;
    for (int i = 0; i < prefixes.size(); i++) {
      if (path.startsWith(prefixes.get(i))) return i;
    }
    return -1;
  }

  private static int majorOf(InputStream in, String name) throws IOException {
    try (DataInputStream data = new DataInputStream(in)) {
      if (data.readInt() != 0xCAFEBABE) throw new IOException(name + " is not a class file");
      data.readUnsignedShort();
      return data.readUnsignedShort();
    }
  }
}
