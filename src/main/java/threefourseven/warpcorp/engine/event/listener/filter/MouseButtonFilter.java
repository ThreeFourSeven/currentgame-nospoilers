package threefourseven.warpcorp.engine.event.listener.filter;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.MouseButtonEvent;
import threefourseven.warpcorp.engine.input.Input;

import java.util.HashSet;
import java.util.Set;

public class MouseButtonFilter implements EventFilter {

  private final Set<String> buttons;
  private final int actionFilter;
  public MouseButtonFilter(Set<String> buttons, int actionFilter) {
    this.buttons = buttons;
    this.actionFilter = actionFilter;
  }

  public MouseButtonFilter(Set<String> buttons) {
    this(buttons, -1);
  }

  public MouseButtonFilter() {
    this(new HashSet<>());
  }

  @Override
  public boolean shouldInclude(Event event) {
    if(event instanceof MouseButtonEvent) {
      return buttonMatch((MouseButtonEvent)event) && actionMatch((MouseButtonEvent)event);
    }
    return false;
  }

  private boolean buttonMatch(MouseButtonEvent event) {
    if(buttons.size() == 0) {
      return true;
    }
    return buttons.contains(Input.mouse.getCodeName(event.getButton()));
  }

  private boolean actionMatch(MouseButtonEvent event) {
    if(actionFilter == -1) {
      return true;
    }
    return event.getAction() == actionFilter;
  }

}
