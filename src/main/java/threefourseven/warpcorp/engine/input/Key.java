package threefourseven.warpcorp.engine.input;

import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.KeyEvent;
import threefourseven.warpcorp.engine.event.listener.filter.KeyFilter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Key {

  private final Map<String, Integer> codes = new HashMap<>();
  private final Map<Integer, Boolean> wasDown = new HashMap<>();
  private final Map<Integer, Boolean> isDown = new HashMap<>();
  private final Map<Integer, Long> downTimestamp = new HashMap<>();
  private final Map<Integer, Long> downDuration = new HashMap<>();

  public String getCodeName(int code) {
    for(String codeName : codes.keySet()) {
      if(codes.get(codeName) == code) {
        return codeName;
      }
    }
    return "";
  }

  public int getCode(String key) {
    return codes.getOrDefault(key, -1);
  }

  public boolean isDown(String key) {
    return isDown.getOrDefault(getCode(key), false);
  }

  public boolean isDown(String button, int minDownTimeMs) {
    return isDown(button) && getDownTimeMs(button) >= minDownTimeMs;
  }

  public boolean wasDown(String key) {
    return wasDown.getOrDefault(getCode(key), false);
  }

  public boolean isClicked(String key) {
    return isDown(key) && !wasDown(key);
  }

  public boolean isReleased(String key) {
    return !isDown(key) && wasDown(key);
  }

  public long getDownTimeMs(String key) {
    return downDuration.getOrDefault(getCode(key), 0L);
  }

  public void initialize() {
    EventState.addEventListener(new KeyFilter().toListener(event -> {
      KeyEvent e = (KeyEvent)event;
      isDown.put(e.getKey(), e.getAction() == GLFW.GLFW_PRESS || e.getAction() == GLFW.GLFW_REPEAT);
    }));
  }

  public void update() {
    for (String key : codes.keySet()) {
      int code = getCode(key);
      boolean isDown = isDown(key);
      boolean isReleased = isReleased(key);

      if (isDown) {
        long now = Instant.now().toEpochMilli();
        if (!downTimestamp.containsKey(code))
          downTimestamp.put(code, now);

        long timestamp = downTimestamp.get(code);
        downDuration.put(code, now - timestamp);
      }

      if (isReleased) {
        downTimestamp.remove(code);
        downDuration.put(code, 0L);
      }

      wasDown.put(code, isDown);
    }
  }

  public void setCodes(Map<String, Integer> codes) {
    this.codes.putAll(codes);
  }


}
