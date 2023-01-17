package threefourseven.warpcorp.engine.logger;

import lombok.Getter;
import threefourseven.warpcorp.engine.util.ConsoleColors;

import java.util.ArrayList;
import java.util.List;

public class Logger {

  @Getter
  protected static final List<LogMessage> messages = new ArrayList<>();

  protected static String getConsoleColor(LogLevel level) {
    switch(level) {
      case Info: return ConsoleColors.GREEN;
      case Debug: return ConsoleColors.PURPLE_BOLD;
      case Warning: return ConsoleColors.YELLOW;
      case Error: return ConsoleColors.RED;
      case Critical: return ConsoleColors.RED_BOLD;
    }
    return ConsoleColors.WHITE;
  }

  protected static String consoleLog(LogLevel level, String message) {
    return getConsoleColor(level) + message + ConsoleColors.RESET;
  }

  protected static void log(LogLevel level, String message) {
    String msg = String.format("[%s] %s", level, message);
    messages.add(new LogMessage(level, msg));
    System.out.println(consoleLog(level, msg));
  }

  public static void info(String message) {
    log(LogLevel.Info, message);
  }

  public static void warning(String message) {
    log(LogLevel.Warning, message);
  }

  public static void debug(String message) {
    log(LogLevel.Debug, message);
  }

  public static void error(String message) {
    log(LogLevel.Error, message);
  }

  public static void critical(String message) {
    log(LogLevel.Critical, message);
  }

  public static List<String> collectMessages() {
    List<String> collectedMessages = new ArrayList<>();
    messages.stream().map(LogMessage::getMessage).forEach(collectedMessages::add);
    return collectedMessages;
  }

}
