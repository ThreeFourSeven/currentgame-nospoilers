package threefourseven.warpcorp.engine.event.producer;

import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.MouseMotionEvent;
import threefourseven.warpcorp.engine.graphics.Window;

public class MouseMotionEventProducer extends EventCallbackProducer {

  private final Window window;
  private float prevX;
  private float prevY;

  public MouseMotionEventProducer(Window window) {
    this.window = window;
  }

  @Override
  public void setCallback() {
    GLFW.glfwSetCursorPosCallback(window.getGlfwId(), (wid, x, y) -> {
      float dx = (float)x - prevX;
      float dy = (float)y - prevY;
      prevX = (float)x;
      prevY = (float)y;
      EventState.captureEvent(new MouseMotionEvent((float)x, (float)y, dx, dy));
    });
  }

}
