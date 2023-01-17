package threefourseven.warpcorp.engine.util;

import java.io.OutputStream;

@FunctionalInterface
public interface ObjectWriter {
  void write(Object object, OutputStream os);
}
