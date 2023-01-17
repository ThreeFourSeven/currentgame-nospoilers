package threefourseven.warpcorp.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import threefourseven.warpcorp.engine.entity.component.*;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.Map;
import java.util.Optional;

public class Entity extends AutoIdentified {

  protected Entity(String id) {
    super(id);
  }
  protected Entity() {
    super();
  }

  @JsonIgnore
  public Optional<EntityTemplate> toTemplate() {
    return SceneUtil.getScene().getEntityTree().getNode(this).map(EntityTreeNode::toTemplate);
  }

  @JsonIgnore
  public Map<String, Component> getComponents() {
    return SceneUtil.getScene().getEntityTree().getComponents(this);
  }

  @JsonIgnore
  public Optional<Component> getComponent(String componentType) {
    return SceneUtil.getScene().getEntityTree().getComponent(this, componentType);
  }

  @JsonIgnore
  public Optional<String> getName() {
    return getComponent("NameComponent").map(c -> ((NameComponent)c).getValue());
  }

  @JsonIgnore
  public Optional<TransformComponent> getTransform() {
    return getComponent("TransformComponent").map(c -> (TransformComponent)c);
  }

  @JsonIgnore
  public Optional<MeshComponent> getMesh() {
    return getComponent("MeshComponent").map(c -> (MeshComponent)c);
  }

  @JsonIgnore
  public Optional<MaterialComponent> getMaterial() {
    return getComponent("MaterialComponent").map(c -> (MaterialComponent)c);
  }

  @JsonIgnore
  public Optional<SpriteLayerComponent> getSpriteLayer() {
    return getComponent("SpriteLayerComponent").map(c -> (SpriteLayerComponent)c);
  }

  @JsonIgnore
  public Optional<AnimatedSpriteComponent> getAnimatedSprite() {
    return getComponent("AnimatedSpriteComponent").map(c -> (AnimatedSpriteComponent)c);
  }

  @JsonIgnore
  public boolean hasComponent(String componentType) {
    return getComponents().containsKey(componentType);
  }

}
