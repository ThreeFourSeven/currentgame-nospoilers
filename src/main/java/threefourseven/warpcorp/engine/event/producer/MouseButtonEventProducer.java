package threefourseven.warpcorp.engine.event.producer;

import lombok.AllArgsConstructor;
import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.MouseButtonEvent;
import threefourseven.warpcorp.engine.graphics.Window;

@AllArgsConstructor
public class MouseButtonEventProducer extends EventCallbackProducer {

  private final Window window;

  @Override
  public void setCallback() {
    if(window != null) {
      GLFW.glfwSetMouseButtonCallback(window.getGlfwId(), (wid, button, action, mods) -> EventState.captureEvent(new MouseButtonEvent(button, action, mods)));
    }
  }

}
