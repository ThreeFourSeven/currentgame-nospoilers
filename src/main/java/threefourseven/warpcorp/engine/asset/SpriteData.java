package threefourseven.warpcorp.engine.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpriteData {

  protected String spriteSheetName;
  protected float sheetOffsetX;
  protected float sheetOffsetY;
  protected float width;
  protected float height;

  public SpriteData() {
    this("default", 0, 0, 1, 1);
  }

}
