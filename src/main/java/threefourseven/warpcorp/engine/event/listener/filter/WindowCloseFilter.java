package threefourseven.warpcorp.engine.event.listener.filter;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.WindowCloseEvent;

public class WindowCloseFilter implements EventFilter {

  @Override
  public boolean shouldInclude(Event event) {
    return event instanceof WindowCloseEvent;
  }

}
