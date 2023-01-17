package threefourseven.warpcorp.engine.event.producer;

import lombok.AllArgsConstructor;
import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.WindowResizeEvent;
import threefourseven.warpcorp.engine.graphics.Window;

@AllArgsConstructor
public class WindowResizeEventProducer extends EventCallbackProducer {

  private final Window window;

  @Override
  public void setCallback() {
    GLFW.glfwSetFramebufferSizeCallback(window.getGlfwId(), (wid, w, h) -> EventState.captureEvent(new WindowResizeEvent(w, h)));
  }

}
