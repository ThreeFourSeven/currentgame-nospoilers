package threefourseven.warpcorp.engine.graphics;

import lombok.Getter;
import org.joml.Vector2f;
import threefourseven.warpcorp.engine.asset.SpriteData;
import threefourseven.warpcorp.engine.asset.SpriteSheet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class SpriteRegistry {

  protected final Map<String, SpriteData> sprites = new HashMap<>();

  protected final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

  public SpriteRegistry register(String name, SpriteData spriteData) {
    sprites.put(name, spriteData);
    return this;
  }


  public SpriteRegistry register(String name, SpriteSheet spriteSheet) {
    spriteSheets.put(name, spriteSheet);
    spriteSheet.getImage().ifPresent(image -> {
      spriteSheet.getSpriteSheetSize().setValue(new Vector2f(image.getWidth(), image.getHeight()));
    });
    return this;
  }

  public Optional<SpriteData> getSpriteData(String name) {
    if(sprites.containsKey(name)) {
      return Optional.of(sprites.get(name));
    }
    return Optional.empty();
  }

  public Optional<SpriteSheet> getSpriteSheet(String name) {
    if(spriteSheets.containsKey(name)) {
      return Optional.of(spriteSheets.get(name));
    }
    return Optional.empty();
  }

}
