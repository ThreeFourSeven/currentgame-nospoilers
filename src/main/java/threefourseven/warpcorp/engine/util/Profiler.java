package threefourseven.warpcorp.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Profiler {

  public static Profiler mainLoopProfiler = new Profiler();

  private final int maxRecordedTimes = 10000;
  private List<Long> times = new ArrayList<>();
  private long timestamp = 0;

  public void start() {
    timestamp = System.nanoTime();
  }

  public long end() {
    long time = System.nanoTime() - timestamp;
    if(times.size() >= maxRecordedTimes)
      times.remove(0);
    times.add(time);
    return time;
  }

  public long getLastNanoDelta() {
    if(times.size() == 0)
      return 0;
    return times.get(times.size() - 1);
  }

  public float getLastMilliDelta() {
    return getLastNanoDelta() / 1_000_000f;
  }

  public long average() {
    if(times.size() == 0)
      return 0L;
    return times.stream().reduce(0L, Long::sum) / times.size();
  }

  public long profile(Runnable runnable) {
    start();
    runnable.run();
    return end();
  }

  public <T> T profile(Supplier<T> supplier) {
    start();
    T value = supplier.get();
    return value;
  }

  public <T> long profile(Consumer<T> consumer, T value) {
    start();
    consumer.accept(value);
    return end();
  }

  public <T, V> V profile(Function<T, V> function, T value) {
    start();
    V result = function.apply(value);
    end();
    return result;
  }

  public static float calculateAverageFrameTimeMs() {
    return Profiler.mainLoopProfiler.average() / 1_000_000f;
  }

  public static float calculateAverageFramesPerSecond() {
    return  1 / (calculateAverageFrameTimeMs() / 1000f);
  }

}
