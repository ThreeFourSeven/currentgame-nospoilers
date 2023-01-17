package threefourseven.warpcorp.engine.event.listener.filter;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.KeyEvent;
import threefourseven.warpcorp.engine.input.Input;

import java.util.HashSet;
import java.util.Set;

public class KeyFilter implements EventFilter {

  private final Set<String> keys;
  private final int actionFilter;
  public KeyFilter(Set<String> keys, int actionFilter) {
    this.keys = keys;
    this.actionFilter = actionFilter;
  }

  public KeyFilter(Set<String> keys) {
    this(keys, -1);
  }

  public KeyFilter() {
    this(new HashSet<>());
  }

  @Override
  public boolean shouldInclude(Event event) {
    if(event instanceof KeyEvent) {
      return keyMatch((KeyEvent)event) && actionMatch((KeyEvent)event);
    }
    return false;
  }

  private boolean keyMatch(KeyEvent event) {
    if(keys.size() == 0) {
      return true;
    }
    return keys.contains(Input.key.getCodeName(event.getKey()));
  }

  private boolean actionMatch(KeyEvent event) {
    if(actionFilter == -1) {
      return true;
    }
    return event.getAction() == actionFilter;
  }

}
