package threefourseven.warpcorp.engine.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtil {

  public static <T> Stream<List<T>> chunk(Stream<T> stream, int size) {
    AtomicInteger index = new AtomicInteger(0);
    return stream.collect(Collectors.groupingBy(x -> index.getAndIncrement() / size))
      .values().stream();
  }

}
