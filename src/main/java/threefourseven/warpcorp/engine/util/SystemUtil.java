package threefourseven.warpcorp.engine.util;

import java.util.Optional;
import java.util.function.Function;

public class SystemUtil {

  public static Optional<String> getEnvironmentVariable(String name) {
    String value = System.getenv(name);
    if(value == null) {
      return Optional.empty();
    }
    return Optional.of(value);
  }

  public static <T> Optional<T> getEnvironmentVariableAs(String name, Function<String, T> parser) {
    return getEnvironmentVariable(name).map(parser);
  }

}
