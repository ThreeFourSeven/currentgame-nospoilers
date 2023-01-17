package threefourseven.warpcorp.engine.util;

import java.io.InputStream;
import java.util.Optional;

@FunctionalInterface
public interface ObjectReader {
  Optional<Object> read(String path, InputStream is);
}
