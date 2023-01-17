package threefourseven.warpcorp.engine.entity;

import threefourseven.warpcorp.engine.asset.Sprite;
import threefourseven.warpcorp.engine.entity.component.*;
import threefourseven.warpcorp.engine.graphics.Graphics;

import java.util.*;
import java.util.function.Supplier;

public class EntityUtil {
  private static final Map<String, Supplier<EntityTemplate>> entityGenerators = new HashMap<>();

  public static void addTemplateGenerator(String name, Supplier<EntityTemplate> generator) {
    entityGenerators.put(name, generator);
  }

  public static Optional<EntityTemplate> generateTemplate(String generatorName) {
    if(entityGenerators.containsKey(generatorName)) {
      return Optional.of(entityGenerators.get(generatorName).get());
    }
    return Optional.empty();
  }

  public static void initialize() {
    EntityUtil.addTemplateGenerator("Quad", () -> new EntityTemplate(List.of(
      new NameComponent("Quad"),
      new TransformComponent(),
      new MeshComponent(Graphics.quadAsset),
      new MaterialComponent(Graphics.defaultMaterial)
    )));

    EntityUtil.addTemplateGenerator("Triangle", () -> new EntityTemplate(List.of(
      new NameComponent("Triangle"),
      new TransformComponent(),
      new MeshComponent(Graphics.triangleAsset),
      new MaterialComponent(Graphics.defaultMaterial)
    )));

    EntityUtil.addTemplateGenerator("AnimatedSprite", () -> new EntityTemplate(List.of(
      new NameComponent("AnimatedSprite"),
      new AnimatedSpriteComponent(new ArrayList<>(), 1000)
    )));

    EntityUtil.addTemplateGenerator("SpriteLayer", () -> new EntityTemplate(List.of(
      new NameComponent("SpriteLayer"),
      new SpriteLayerComponent(new ArrayList<>(), 0, 0)
    )));
  }

}
