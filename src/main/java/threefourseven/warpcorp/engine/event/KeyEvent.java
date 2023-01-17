package threefourseven.warpcorp.engine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import threefourseven.warpcorp.engine.util.AutoIdentified;

@Getter
@AllArgsConstructor
@ToString
public class KeyEvent extends AutoIdentified implements Event {

  private final int key;
  private final int scanCode;
  private final int action;
  private final int mods;

  @Override
  public boolean isSingleton() {
    return false;
  }
}
