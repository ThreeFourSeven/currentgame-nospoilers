package threefourseven.warpcorp.engine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import threefourseven.warpcorp.engine.util.AutoIdentified;

@Getter
@AllArgsConstructor
@ToString
public class MouseMotionEvent extends AutoIdentified implements Event {

  private final float x;
  private final float y;
  private final float dx;
  private final float dy;

  @Override
  public boolean isSingleton() {
    return true;
  }

}
