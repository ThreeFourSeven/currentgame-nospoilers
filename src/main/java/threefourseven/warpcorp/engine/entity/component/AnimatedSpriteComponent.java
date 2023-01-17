package threefourseven.warpcorp.engine.entity.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.WarpCorp;
import threefourseven.warpcorp.engine.asset.Sprite;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class AnimatedSpriteComponent extends AutoIdentified implements Component {

  protected static final Map<Integer, Float> frameTimeCounters = new HashMap<>();

  protected List<Sprite> sprites;
  protected int frameTimeMs;

  protected float x;

  protected float y;

  @JsonIgnore
  protected int frame = 0;

  public AnimatedSpriteComponent(List<Sprite> sprites, int frameTimeMs) {
    this.sprites = sprites;
    this.frameTimeMs = frameTimeMs;
  }

  public AnimatedSpriteComponent() {
    this(new ArrayList<>(), 1000);
  }

  public void update() {
    float counter = getCounter();
    if(counter >= frameTimeMs) {
      frame++;
      if(frame >= sprites.size())
        frame = 0;
    }
  }

  @Override
  public Component copy() {
    return new AnimatedSpriteComponent(
      sprites.stream()
        .map(sprite -> new Sprite(sprite.getSpriteSheetName(), sprite.getName(), sprite.getX(), sprite.getY(), sprite.getZ(), sprite.getScaleX(), sprite.getScaleY()))
        .collect(Collectors.toList()),
      frameTimeMs
    );
  }

  @JsonIgnore
  public Optional<Sprite> getSprite() {
    if(frame < sprites.size()) {
      Sprite sprite = sprites.get(frame);
      return Optional.of(new Sprite(sprite.getSpriteSheetName(), sprite.getName(), sprite.getX() + x, sprite.getY() + y, sprite.getZ(), sprite.getScaleX(), sprite.getScaleY()));
    }
    return Optional.empty();
  }

  private float getCounter() {
    if(!frameTimeCounters.containsKey(frameTimeMs)) {
      frameTimeCounters.put(frameTimeMs, 0f);
    }
    return frameTimeCounters.get(frameTimeMs);
  }

  public static void updateFrameTimeCounters() {
    for(Integer frameTime : frameTimeCounters.keySet()) {
      float counter = frameTimeCounters.get(frameTime);
      counter += WarpCorp.getFrameTimeMs();
      frameTimeCounters.put(frameTime, counter);
    }
  }

  public static void resetFrameTimeCounters() {
    for(Integer frameTime : frameTimeCounters.keySet()) {
      float counter = frameTimeCounters.get(frameTime);
      if(counter >= frameTime) {
        frameTimeCounters.put(frameTime, 0f);
      }
    }
  }

}
