package threefourseven.warpcorp.engine.entity;

import lombok.Getter;
import threefourseven.warpcorp.engine.entity.component.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class EntityTemplate {

  protected Collection<Component> components;
  protected Collection<EntityTemplate> children = new ArrayList<>();

  public EntityTemplate(Collection<Component> components) {
    this.components = components;
  }

  public EntityTemplate() {
    this(new ArrayList<>());
  }

  public EntityTemplate addChild(Collection<Component> components) {
    EntityTemplate child = new EntityTemplate(components);
    children.add(child);
    return child;
  }

  public EntityTemplate copyFromNode(EntityTreeNode node, EntityTemplate parent) {
    if(parent == null) {
      components = node.getEntity().getComponents().values()
        .stream().map(Component::copy).collect(Collectors.toList());
      for(EntityTreeNode childNode : node.getChildren()) {
        this.copyFromNode(childNode, this);
      }
    } else {
      EntityTemplate child = parent.addChild(node.getEntity().getComponents().values()
        .stream().map(Component::copy).collect(Collectors.toList()));
      for(EntityTreeNode childNode : node.getChildren()) {
        child.copyFromNode(childNode, child);
      }
    }
    return this;
  }

  public EntityTemplate copyFromNode(EntityTreeNode node) {
    return copyFromNode(node, null);
  }

}
