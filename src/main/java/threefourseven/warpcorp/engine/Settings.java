package threefourseven.warpcorp.engine;

import threefourseven.warpcorp.engine.io.IO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Settings {
  protected static Map<String, Object> settings = new HashMap<>();

  public static void load(String path) {
    IO.readFileAs(path, settings.getClass()).ifPresent(map -> settings = map);
  }

  public static void save(String path) {
    IO.writeToFile(path, settings);
  }

  public static Optional<Object> getSetting(String key) {
    if(settings.containsKey(key)) {
      return Optional.of(settings.get(key));
    }
    return Optional.empty();
  }

  public static <T> Optional<T> getAs(String key) {
    return getSetting(key).map(o -> (T)o);
  }

  public static <T> Optional<T> getAs(String key, Class<T> tClass) {
    return getAs(key);
  }


}
