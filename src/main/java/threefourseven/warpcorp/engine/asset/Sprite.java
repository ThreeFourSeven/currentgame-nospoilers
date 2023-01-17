package threefourseven.warpcorp.engine.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.util.Named;

@Getter
@Setter
@AllArgsConstructor
public class Sprite implements Named {

  protected String spriteSheetName;

  protected String name;

  protected float x;

  protected float y;

  protected float z;

  protected float scaleX;

  protected float scaleY;

  public Sprite() {
    this("default", "default", 0, 0, 0, 1, 1);
  }

}
