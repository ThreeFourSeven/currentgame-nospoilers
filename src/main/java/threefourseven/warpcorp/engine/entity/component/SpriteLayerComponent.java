package threefourseven.warpcorp.engine.entity.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.asset.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class SpriteLayerComponent implements Component {

  protected List<Sprite> sprites;

  protected float x;

  protected float y;

  public SpriteLayerComponent() {
    this(new ArrayList<>(), 0, 0);
  }

  @Override
  public SpriteLayerComponent copy() {
    return new SpriteLayerComponent(
      sprites.stream()
        .map(sprite -> new Sprite(sprite.getSpriteSheetName(), sprite.getName(), sprite.getX(), sprite.getY(), sprite.getZ(), sprite.getScaleX(), sprite.getScaleY()))
        .collect(Collectors.toList()),
      x, y
    );
  }

  @JsonIgnore
  public List<Sprite> getSpritesShifted() {
    return sprites.stream()
      .map(sprite -> new Sprite(sprite.getSpriteSheetName(), sprite.getName(), sprite.getX() + x, sprite.getY() + y, sprite.getZ(), sprite.getScaleX(), sprite.getScaleY()))
      .collect(Collectors.toList());
  }

}
