package threefourseven.warpcorp.engine.event.listener.filter;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.MouseMotionEvent;

public class MouseMotionFilter implements EventFilter {

  @Override
  public boolean shouldInclude(Event event) {
    return event instanceof MouseMotionEvent;
  }

}
