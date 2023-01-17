package threefourseven.warpcorp.engine.event.producer;

import lombok.AllArgsConstructor;
import threefourseven.warpcorp.engine.event.WindowCloseEvent;
import threefourseven.warpcorp.engine.graphics.Window;

@AllArgsConstructor
public class WindowCloseEventProducer extends EventConditionalProducer {

  private final Window window;

  @Override
  protected boolean shouldProduce() {
    return window.shouldClose();
  }

  @Override
  protected WindowCloseEvent produce() {
    return new WindowCloseEvent();
  }

}
