package ru.cwcode.commands.paperplatform.features;

import org.bukkit.entity.Player;
import ru.cwcode.commands.BrigadierConformance;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.paperplatform.PaperMain;
import ru.cwcode.commands.paperplatform.executor.AutowiredExecutor;
import ru.cwcode.commands.paperplatform.paper.PaperPlatform;
import ru.cwcode.commands.paperplatform.paper.PaperSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code /commandstest brigadier [игрок]} — прогон всех команд, зарегистрированных через CWCommands,
 * через настоящий Brigadier ({@link BrigadierConformance}) от имени отправителя или указанного игрока.
 * Команды не исполняются. Подробности — в {@code plugins/CWCommands/brigadier-check.txt}.
 */
public class BrigadierCheckCommand extends AutowiredExecutor {
  public void check() {
    report(sender);
  }

  public void check(Player target) {
    report(new PaperSender(target));
  }

  private void report(Sender who) {
    List<String> lines = new ArrayList<>();
    lines.add("Проверка Brigadier " + LocalDateTime.now() + " от имени " + who.getName());

    int errors = 0;
    int highlighting = 0;
    List<String> failed = new ArrayList<>();

    for (Command command : ((PaperPlatform) CommandsAPI.getPlatform()).registeredCommands()) {
      BrigadierConformance.Report report;
      try {
        report = BrigadierConformance.check(command, who);
      } catch (RuntimeException e) {
        lines.add("");
        lines.add("/" + command.getName() + ": проверка упала " + e);
        failed.add(command.getName());
        errors++;
        continue;
      }

      errors += report.errors().size();
      highlighting += report.highlighting().size();
      if (!report.clean()) failed.add(command.getName());

      lines.add("");
      lines.add(report.toString());
      report.errors().forEach(line -> lines.add("  ОШИБКА " + line));
      report.warnings().forEach(line -> lines.add("  предупреждение " + line));
      report.highlighting().forEach(line -> lines.add("  подсветка " + line));
    }

    Path file = PaperMain.plugin.getDataFolder().toPath().resolve("brigadier-check.txt");
    try {
      Files.createDirectories(file.getParent());
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException e) {
      PaperMain.plugin.getLogger().warning("brigadier-check.txt не записан: " + e);
    }

    String summary = "Brigadier: команд " + ((PaperPlatform) CommandsAPI.getPlatform()).registeredCommands().size()
                     + ", ошибок " + errors + (failed.isEmpty() ? "" : " в " + failed) + ", мимо подсветки " + highlighting
                     + ". Отчёт: " + file;
    PaperMain.plugin.getLogger().info(summary);
    sender.sendMessage(summary);
  }
}
