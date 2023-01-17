package threefourseven.warpcorp.engine.event.producer;

import lombok.AllArgsConstructor;
import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.KeyEvent;
import threefourseven.warpcorp.engine.graphics.Window;

@AllArgsConstructor
public class KeyEventProducer extends EventCallbackProducer {

  private final Window window;

  @Override
  public void setCallback() {
    if(window != null) {
      GLFW.glfwSetKeyCallback(window.getGlfwId(), (wid, key, scanCode, action, mods) -> EventState.captureEvent(new KeyEvent(key, scanCode, action, mods)));
    }
  }

}
