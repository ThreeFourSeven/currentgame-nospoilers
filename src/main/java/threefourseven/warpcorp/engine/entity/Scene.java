package threefourseven.warpcorp.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import threefourseven.warpcorp.engine.entity.component.Component;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class Scene extends AutoIdentified {

  protected final EntityTree entityTree;

  public Scene() {
    entityTree = new EntityTree();
    entityTree.setRoot(new EntityTreeNode(new Entity(), null));
  }

  public Entity createEntity(Entity parent, Collection<Component> components) {
    Entity entity = new Entity();

    entityTree.getNode(parent).ifPresent(parentNode -> entityTree.addTo(entity, parentNode));

    for(Component component : components)
      entityTree.attachComponent(entity, component);

    Optional<Component> parentTOp = entityTree.getComponent(parent, "TransformComponent");
    Optional<Component> childTOp = entityTree.getComponent(entity, "TransformComponent");
    if(parentTOp.isPresent() && childTOp.isPresent()) {
      TransformComponent parentT = (TransformComponent)parentTOp.get();
      TransformComponent childT = (TransformComponent)childTOp.get();
      childT.updateAbsoluteMatrix(parentT.getAbsoluteMatrix());
    }

    return entity;
  }

  public Entity createEntity(Collection<Component> components) {
    return createEntity(getRoot(), components);
  }

  public void removeEntity(Entity entity) {
    entityTree.getNode(entity).ifPresent(entityTree::remove);
  }

  public void removeAllChildren(Entity entity) {
    entityTree.getNode(entity).ifPresent(entityTree::removeChildren);
  }

  public Entity createEntityFromTemplate(Entity parent, EntityTemplate template) {
    Entity root = createEntity(parent, template.getComponents());
    for(EntityTemplate childTemplate : template.getChildren()) {
      createEntityFromTemplate(root, childTemplate);
    }
    return root;
  }

  public Entity createEntityFromTemplate(EntityTemplate template) {
    return createEntityFromTemplate(getRoot(), template);
  }

  @JsonIgnore
  public Entity getRoot() {
    return entityTree.getRoot().getEntity();
  }

  @JsonIgnore
  public EntityTemplate toTemplate() {
    return getRoot().toTemplate().get();
  }

  public Optional<Entity> getParent(Entity entity) {
    Optional<EntityTreeNode> nodeOp = entityTree.getNode(entity);
    if(nodeOp.isPresent()) {
      EntityTreeNode node = nodeOp.get();
      if(node.parent != null)
        return Optional.of(node.parent);
    }
    return Optional.empty();
  }

  public Optional<Entity> getEntity(String id) {
    return entityTree.getEntity(id);
  }

  public void walk(Consumer<Entity> walker) {
    entityTree.getRoot().walkDown(node -> walker.accept(node.entity));
  }

}
