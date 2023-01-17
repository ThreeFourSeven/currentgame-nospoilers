package threefourseven.warpcorp.engine.entity;

import lombok.Getter;
import threefourseven.warpcorp.engine.asset.Material;
import threefourseven.warpcorp.engine.asset.Mesh;
import threefourseven.warpcorp.engine.asset.Sprite;
import threefourseven.warpcorp.engine.entity.component.*;
import threefourseven.warpcorp.engine.graphics.SpriteRegistry;
import threefourseven.warpcorp.engine.io.IO;

import java.util.*;
import java.util.function.Consumer;

public class SceneUtil {
  @Getter
  private static Scene scene = new Scene();
  private static final Map<String, Map<String, Set<String>>> renderTree = new HashMap<>();
  private static final Map<String, Mesh> meshes = new HashMap<>();
  private static final Map<String, Material> materials = new HashMap<>();
  private static final Map<String, TransformComponent> transforms = new HashMap<>();

  private static final Collection<Sprite> sprites = new ArrayList<>();

  public static Entity createEntity(Entity parent, Collection<Component> components) {
    return scene.createEntity(parent, components);
  }

  public static Entity createEntity(Collection<Component> components) {
    return scene.createEntity(components);
  }

  public static void removeEntity(Entity entity) {
    scene.removeEntity(entity);
  }

  public static Entity createEntityFromTemplate(Entity parent, EntityTemplate template) {
    return scene.createEntityFromTemplate(parent, template);
  }

  public static Entity createEntityFromTemplate(EntityTemplate template) {
    return scene.createEntityFromTemplate(template);
  }

  public static void generateEntity(Entity parent, String generatorName) {
    EntityUtil.generateTemplate(generatorName)
      .ifPresent(template -> createEntityFromTemplate(parent, template));
  }

  public static void generateEntity(String generatorName) {
    EntityUtil.generateTemplate(generatorName).ifPresent(SceneUtil::createEntityFromTemplate);
  }

  public static void walkScene(Consumer<Entity> walker) {
    scene.walk(walker);
  }

  public static void updateTransforms(Entity entity) {
    scene.getEntityTree().getNode(entity).ifPresent(root -> root.walkDown(node -> {
      node.entity.getTransform().ifPresent(TransformComponent::updateRelativeMatrix);
      if(node.parent != null) {
        node.parent.getTransform().ifPresent(parentTransform ->
          node.entity.getTransform().ifPresent(transform -> transform.updateAbsoluteMatrix(parentTransform.getAbsoluteMatrix()))
        );
      }
    }));
  }

  public static void saveScene(String path) {
    IO.writeToFile(path, scene);
  }

  public static void loadScene(String path) {
    scene = IO.readFileAs(path, Scene.class).orElse(new Scene());
    updateTransforms(scene.getRoot());
  }

  public static int getEntityCount() {
    return getScene().getEntityTree().getAsList().size();
  }

  // material -> mesh -> set<entity>
  public static SceneMeshRenderLookups getSceneMeshRenderLookups() {
    renderTree.clear();
    meshes.clear();
    transforms.clear();
    materials.clear();

    walkScene(entity -> entity.getMaterial().flatMap(MaterialComponent::getMaterial).ifPresent(material -> {
      if (!renderTree.containsKey(material.getId())) {
        renderTree.put(material.getId(), new HashMap<>());
      }
      materials.put(material.getId(), material);
      Map<String, Set<String>> meshSubTree = renderTree.get(material.getId());
      entity.getMesh().flatMap(MeshComponent::getMesh).ifPresent(mesh -> {
        if (!meshSubTree.containsKey(mesh.getId())) {
          meshSubTree.put(mesh.getId(), new HashSet<>());
        }
        meshes.put(mesh.getId(), mesh);
        entity.getTransform().ifPresent(transform -> {
          transforms.put(transform.getId(), transform);
          meshSubTree.get(mesh.getId()).add(transform.getId());
        });
      });
    }));

    return new SceneMeshRenderLookups(renderTree, meshes, materials, transforms);
  }

  public static Collection<Sprite> getSceneSprites() {
    sprites.clear();
    walkScene(entity -> {
      entity.getSpriteLayer().map(SpriteLayerComponent::getSpritesShifted).ifPresent(sprites::addAll);
      entity.getAnimatedSprite().ifPresent(animatedSpriteComponent -> {
        animatedSpriteComponent.update();
        animatedSpriteComponent.getSprite().ifPresent(sprites::add);
      });
    });
    return sprites;
  }

}
