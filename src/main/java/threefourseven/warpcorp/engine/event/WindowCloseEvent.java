package threefourseven.warpcorp.engine.event;

import threefourseven.warpcorp.engine.util.AutoIdentified;

public class WindowCloseEvent extends AutoIdentified implements Event {

  @Override
  public boolean isSingleton() {
    return true;
  }

}
