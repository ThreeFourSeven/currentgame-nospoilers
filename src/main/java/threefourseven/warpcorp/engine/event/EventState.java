package threefourseven.warpcorp.engine.event;

import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.Settings;
import threefourseven.warpcorp.engine.event.listener.EventListener;
import threefourseven.warpcorp.engine.event.listener.filter.EventFilter;
import threefourseven.warpcorp.engine.event.producer.EventCallbackProducer;
import threefourseven.warpcorp.engine.event.producer.EventConditionalProducer;
import threefourseven.warpcorp.engine.event.producer.EventProducer;
import threefourseven.warpcorp.engine.input.Input;
import threefourseven.warpcorp.engine.logger.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EventState {

  private static final Map<String, EventProducer> eventProducers = new HashMap<>();

  private static final Map<String, EventListener> eventListeners = new HashMap<>();

  private static final Set<Event> events = new HashSet<>();

  public static void poll() {
    Input.update();
    events.clear();
    GLFW.glfwPollEvents();

    for(EventProducer producer : eventProducers.values()) {
      if(producer instanceof EventConditionalProducer) {
        ((EventConditionalProducer)producer).update();
      }
    }

    for(Event event : events) {
      for(EventListener listener : eventListeners.values()) {
        if(listener.getFilter().shouldInclude(event)) {
          listener.getConsumer().accept(event);
        }
      }
    }
  }

  public static void addEventProducer(EventProducer producer) {
    if(producer instanceof EventCallbackProducer) {
      ((EventCallbackProducer)producer).setCallback();
    }
    eventProducers.put(producer.getId(), producer);
  }

  public static void addEventListener(EventListener listener) {
    eventListeners.put(listener.getId(), listener);
  }

  public static void addEventListener(EventFilter filter, Consumer<Event> consumer) {
    addEventListener(filter.toListener(consumer));
  }

  public static void captureEvent(Event event) {
    if(event.isSingleton()) {
      events.removeIf(e -> e.getClass().equals(event.getClass()));
    }
    events.add(event);
    if(Settings.getAs("logEvents", Boolean.class).orElse(false)) {
      Logger.debug(String.format("Captured %s event %s", event.getClass().getSimpleName(), event));
    }
  }

}
