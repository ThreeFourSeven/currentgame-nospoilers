package threefourseven.warpcorp.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import threefourseven.warpcorp.engine.entity.component.Component;
import threefourseven.warpcorp.engine.entity.component.NameComponent;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;

import java.util.*;

public class EntityTree {

  @Getter
  protected EntityTreeNode root;

  @Getter
  protected final Map<String, Map<String, Component>> components = new HashMap<>();

  public EntityTree(EntityTreeNode root) {
    if(root != null) {
      setRoot(root);
    }
  }

  //Only for serialization do not use unless aware of null root!
  public EntityTree() {
    this(null);
  }

  public void setRoot(EntityTreeNode node) {
    root = node;
    components.put(root.entity.getId(), Map.of(
      "NameComponent", new NameComponent("Root"),
      "TransformComponent", new TransformComponent()
    ));
  }

  public void add(Entity entity) {
    addTo(entity, root);
  }

  public void addTo(Entity entity, EntityTreeNode parent) {
    EntityTreeNode node = new EntityTreeNode(entity, parent.entity);
    parent.addChild(node);
    components.put(entity.getId(), new HashMap<>());
  }

  public void attachComponent(Entity entity, Component component) {
    components.get(entity.getId()).put(component.getClass().getSimpleName(), component);
  }

  public void remove(EntityTreeNode node) {
    if(node.equals(root))
      return;

    getNode(node.parent).ifPresent(parent -> parent.removeChild(node));
    components.remove(node.entity.getId());
  }

  public void removeChildren(EntityTreeNode node) {
    node.children.forEach(this::removeChildren);
    new HashSet<>(node.children).forEach(this::remove);
  }

  public void clear() {
    root.children.clear();
    components.clear();
    components.put(root.entity.getId(), new HashMap<>());
  }

  public void detachComponent(Entity entity, String componentType) {
    components.get(entity.getId()).remove(componentType);
  }

  public void move(EntityTreeNode node, EntityTreeNode destinationNode) {
    if(node.equals(root))
      return;

    getNode(node.parent).ifPresent(parent -> parent.removeChild(node));
    destinationNode.addChild(node);
  }

  public Optional<EntityTreeNode> getNode(Entity entity) {
    return getAsList().stream().filter(node -> node.entity.equals(entity)).findFirst();
  }

  public Optional<Entity> getEntity(String id) {
    return getAsList().stream().filter(node -> node.entity.getId().equals(id)).map(EntityTreeNode::getEntity).findFirst();
  }

  public Collection<EntityTreeNode> getAsList(EntityTreeNode root) {
    List<EntityTreeNode> treeNodes = new ArrayList<>();
    root.walkDown(treeNodes::add);
    return treeNodes;
  }

  @JsonIgnore
  public Collection<EntityTreeNode> getAsList() {
    Collection<EntityTreeNode> nodes = new HashSet<>();
    root.walkDown(nodes::add);
    return nodes;
  }

  public Map<String, Component> getComponents(Entity entity) {
    return components.get(entity.getId());
  }

  public Optional<Component> getComponent(Entity entity, String componentType) {
    Map<String, Component> components = getComponents(entity);
    if(components.containsKey(componentType)) {
      return Optional.of(components.get(componentType));
    }
    return Optional.empty();
  }

  public Optional<String> getName(Entity entity) {
    return getComponent(entity, "NameComponent").map(c -> ((NameComponent)c).getValue());
  }

  public Optional<TransformComponent> getTransform(Entity entity) {
    return getComponent(entity, "TransformComponent").map(c -> (TransformComponent)c);
  }

  public boolean hasComponent(Entity entity, String componentType) {
    return getComponents(entity).containsKey(componentType);
  }

}
