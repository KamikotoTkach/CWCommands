package ru.cwcode.commands.velocityplatform.features;

import com.velocitypowered.api.proxy.Player;
import ru.cwcode.commands.BrigadierConformance;
import ru.cwcode.commands.Command;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.velocityplatform.VelocityMain;
import ru.cwcode.commands.velocityplatform.executor.AutowiredExecutor;
import ru.cwcode.commands.velocityplatform.velocity.VelocityPlatform;
import ru.cwcode.commands.velocityplatform.velocity.VelocitySender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@code /commandstestv brigadier [игрок]} — прогон всех команд прокси, зарегистрированных через CWCommands,
 * через настоящий Brigadier ({@link BrigadierConformance}) от имени отправителя или указанного игрока.
 * Проверяется полное дерево, которое получает клиент: ввод не должен давать ванильную ошибку, значения —
 * подсвечиваться. Серверный узел (метка + хвост) проверять нечего. Команды не исполняются. Подробности —
 * в {@code plugins/cwcommands/brigadier-check.txt}.
 */
public class BrigadierCheckCommand extends AutowiredExecutor {
  public void check() {
    report(sender);
  }

  public void check(String target) {
    Optional<Player> player = VelocityMain.getPlatform().getServer().getPlayer(target);
    if (player.isEmpty()) {
      sender.sendMessage("Игрок " + target + " не в сети");
      return;
    }
    report(new VelocitySender(player.get()));
  }

  private void report(Sender who) {
    VelocityPlatform platform = VelocityMain.getPlatform();
    List<String> lines = new ArrayList<>();
    lines.add("Проверка Brigadier (Velocity) " + LocalDateTime.now() + " от имени " + who.getName());

    int errors = 0;
    int highlighting = 0;
    List<String> failed = new ArrayList<>();

    for (Command command : platform.registeredCommands()) {
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

    Path file = platform.getPlugin().getDataFolder().resolve("brigadier-check.txt");
    try {
      Files.createDirectories(file.getParent());
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException e) {
      platform.getLogger().warn("brigadier-check.txt не записан: " + e);
    }

    String summary = "Brigadier: команд " + platform.registeredCommands().size()
                     + ", ошибок " + errors + (failed.isEmpty() ? "" : " в " + failed) + ", мимо подсветки " + highlighting
                     + ". Отчёт: " + file;
    platform.getLogger().info(summary);
    sender.sendMessage(summary);
  }
}
