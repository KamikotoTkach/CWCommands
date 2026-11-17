package ru.cwcode.commands.api;

import ru.cwcode.commands.Command;
import ru.cwcode.cwutils.logger.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Platform {
  public abstract void registerCommand(Command command);

  public abstract Logger getLogger();

  public abstract void handleExecutionException(Exception exception, Command command, Sender sender);

  /**
   * Текст исключения, который можно показать отправителю, или null.
   * IllegalArgument/IllegalState исполнители бросают как сообщение игроку («Unknown world: …»);
   * текст прочих (SQL, NPE) — внутренности, игроку его не показываем.
   */
  protected static String userFacingMessage(Exception exception) {
    if (exception instanceof IllegalArgumentException || exception instanceof IllegalStateException) {
      return exception.getLocalizedMessage();
    }
    return null;
  }

  protected void logExecutionError(String header, Command command, Exception exception) {
    StringWriter trace = new StringWriter();
    exception.printStackTrace(new PrintWriter(trace));

    String path = command == null ? "?" : "/" + command.getFullCommandPath(command.getRootCommand().getName());
    getLogger().error(header + " " + path + System.lineSeparator() + trace);
  }
}
