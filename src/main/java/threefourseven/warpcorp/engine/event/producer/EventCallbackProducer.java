package threefourseven.warpcorp.engine.event.producer;

import threefourseven.warpcorp.engine.util.AutoIdentified;

public abstract class EventCallbackProducer extends AutoIdentified implements EventProducer {
  public abstract void setCallback();
}
