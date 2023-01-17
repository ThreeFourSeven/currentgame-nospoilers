package threefourseven.warpcorp.engine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import threefourseven.warpcorp.engine.util.AutoIdentified;

@Getter
@AllArgsConstructor
@ToString
public class WindowResizeEvent extends AutoIdentified implements Event {

  private final int width;
  private final int height;

  @Override
  public boolean isSingleton() {
    return true;
  }

}
