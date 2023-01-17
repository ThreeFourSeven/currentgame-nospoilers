package threefourseven.warpcorp.engine.event.producer;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.util.AutoIdentified;

public abstract class EventConditionalProducer extends AutoIdentified implements EventProducer {
  protected abstract boolean shouldProduce();

  protected abstract Event produce();

  public void update() {
    if(shouldProduce()) {
      EventState.captureEvent(produce());
    }
  }
}
