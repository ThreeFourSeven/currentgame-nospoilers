package threefourseven.warpcorp.engine.event.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.listener.filter.EventFilter;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class EventListener extends AutoIdentified {

  protected final EventFilter filter;
  protected final Consumer<Event> consumer;

}
