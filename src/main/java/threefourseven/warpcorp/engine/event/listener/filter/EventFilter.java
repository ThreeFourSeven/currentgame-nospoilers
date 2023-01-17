package threefourseven.warpcorp.engine.event.listener.filter;

import threefourseven.warpcorp.engine.event.Event;
import threefourseven.warpcorp.engine.event.listener.EventListener;

import java.util.function.Consumer;

@FunctionalInterface
public interface EventFilter {
  boolean shouldInclude(Event event);

  default EventListener toListener(Consumer<Event> consumer) {
    return new EventListener(this, consumer);
  }

  default EventListener toListener(Runnable runnable) {
    return new EventListener(this, e -> runnable.run());
  }
}
