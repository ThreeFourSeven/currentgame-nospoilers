package threefourseven.warpcorp.engine.event.listener.filter;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.MouseScrollEvent;

public class MouseScrollFilter implements EventFilter {

  @Override
  public boolean shouldInclude(Event event) {
    return event instanceof MouseScrollEvent;
  }

}
