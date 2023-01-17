package threefourseven.warpcorp.engine.event;

import threefourseven.warpcorp.engine.util.Identified;

public interface Event extends Identified {
  boolean isSingleton();
}
