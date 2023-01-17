package threefourseven.warpcorp.engine.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogMessage {
  private final LogLevel level;
  private final String message;
}
