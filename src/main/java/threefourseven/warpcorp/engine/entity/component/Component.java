package threefourseven.warpcorp.engine.entity.component;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import threefourseven.warpcorp.engine.util.Copier;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  property = "name")
@JsonSubTypes({
  @JsonSubTypes.Type(value = NameComponent.class, name = "NameComponent"),
  @JsonSubTypes.Type(value = TransformComponent.class, name = "TransformComponent"),
  @JsonSubTypes.Type(value = MeshComponent.class, name = "MeshComponent"),
  @JsonSubTypes.Type(value = MaterialComponent.class, name = "MaterialComponent"),
  @JsonSubTypes.Type(value = SpriteLayerComponent.class, name = "SpriteLayerComponent"),
  @JsonSubTypes.Type(value = AnimatedSpriteComponent.class, name = "AnimatedSpriteComponent")
})
public interface Component extends Copier {
  Component copy();
}
