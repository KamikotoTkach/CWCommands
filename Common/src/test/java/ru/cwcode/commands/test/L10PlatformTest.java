package ru.cwcode.commands.test;

import ru.cwcode.cwutils.l10n.L10nPlatform;
import ru.cwcode.cwutils.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

public class L10PlatformTest implements L10nPlatform {
  Logger logger = new SoutLogger();
  
  public L10PlatformTest() {
  }
  
  @Override
  public File getFile() {
    return new File("target/");
  }
  
  @Override
  public Path getDataDirectory() {
    return new File("test-data/").toPath();
  }
  
  @Override
  public Logger getLogger() {
    return logger;
  }
  
  @Override
  public InputStream getResource(String path) {
    try {
      return new FileInputStream("ru/cwcode/commands/test/data/" + path);
    } catch (FileNotFoundException e) {
      return null;
    }
  }
}
