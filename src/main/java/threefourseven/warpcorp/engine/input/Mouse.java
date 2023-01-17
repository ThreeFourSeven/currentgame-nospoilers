package threefourseven.warpcorp.engine.input;

import lombok.Getter;
import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.MouseButtonEvent;
import threefourseven.warpcorp.engine.event.MouseMotionEvent;
import threefourseven.warpcorp.engine.event.MouseScrollEvent;
import threefourseven.warpcorp.engine.event.listener.filter.MouseButtonFilter;
import threefourseven.warpcorp.engine.event.listener.filter.MouseMotionFilter;
import threefourseven.warpcorp.engine.event.listener.filter.MouseScrollFilter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Mouse {

  private final Map<String, Integer> codes = new HashMap<>();
  private final Map<Integer, Boolean> wasDown = new HashMap<>();
  private final Map<Integer, Boolean> isDown = new HashMap<>();
  private final Map<Integer, Long> downTimestamp = new HashMap<>();
  private final Map<Integer, Long> downDuration = new HashMap<>();
  private float x;
  private float y;
  private float dx;
  private float dy;
  private float scrollX;
  private float scrollY;

  public String getCodeName(int code) {
    for(String codeName : codes.keySet()) {
      if(codes.get(codeName) == code) {
        return codeName;
      }
    }
    return "";
  }
  public int getCode(String button) {
    return codes.getOrDefault(button, -1);
  }

  public boolean isDown(String button) {
    return isDown.getOrDefault(getCode(button), false);
  }

  public boolean isDown(String button, int minDownTimeMs) {
    return isDown(button) && getDownTimeMs(button) >= minDownTimeMs;
  }

  public boolean wasDown(String button) {
    return wasDown.getOrDefault(getCode(button), false);
  }

  public boolean isClicked(String button) {
    return isDown(button) && !wasDown(button);
  }

  public boolean isReleased(String button) {
    return !isDown(button) && wasDown(button);
  }

  public long getDownTimeMs(String button) {
    return downDuration.getOrDefault(getCode(button), 0L);
  }

  public void initialize() {
    EventState.addEventListener(new MouseMotionFilter().toListener(event -> {
      MouseMotionEvent e = (MouseMotionEvent)event;
      x = e.getX();
      y = e.getY();
      dx = e.getDx();
      dy = e.getDy();
    }));

    EventState.addEventListener(new MouseScrollFilter().toListener(event -> {
      MouseScrollEvent e = (MouseScrollEvent)event;
      scrollX = e.getScrollX();
      scrollY = e.getScrollY();
    }));

    EventState.addEventListener(new MouseButtonFilter().toListener(event -> {
      MouseButtonEvent e = (MouseButtonEvent)event;
      isDown.put(e.getButton(), e.getAction() == GLFW.GLFW_PRESS || e.getAction() == GLFW.GLFW_REPEAT);
    }));
  }

  public void update() {
    for (String key : codes.keySet()) {
      int code = getCode(key);
      boolean isDown = isDown(key);
      boolean isReleased = isReleased(key);
      boolean isClicked = isClicked(key);

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
      dx = 0;
      dy = 0;
      scrollX = 0;
      scrollY = 0;
    }
  }

  public void setCodes(Map<String, Integer> codes) {
    this.codes.putAll(codes);
  }

}
