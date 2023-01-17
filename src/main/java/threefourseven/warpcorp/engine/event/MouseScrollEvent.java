package threefourseven.warpcorp.engine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import threefourseven.warpcorp.engine.util.AutoIdentified;

@Getter
@AllArgsConstructor
@ToString
public class MouseScrollEvent extends AutoIdentified implements Event {

  private final float scrollX;
  private final float scrollY;

  @Override
  public boolean isSingleton() {
    return false;
  }
}
