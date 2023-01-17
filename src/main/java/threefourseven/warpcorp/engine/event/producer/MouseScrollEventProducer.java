package threefourseven.warpcorp.engine.event.producer;

import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.MouseScrollEvent;
import threefourseven.warpcorp.engine.graphics.Window;

public class MouseScrollEventProducer extends EventCallbackProducer {

  private final Window window;

  public MouseScrollEventProducer(Window window) {
    this.window = window;
  }

  @Override
  public void setCallback() {
    GLFW.glfwSetScrollCallback(window.getGlfwId(), (wid, sx, sy) -> {
      EventState.captureEvent(new MouseScrollEvent((float)sx, (float)sy));
    });
  }

}
