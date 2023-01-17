package threefourseven.warpcorp.engine.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@Setter
public class EntityTreeNode implements Comparable<EntityTreeNode> {

  protected Entity entity;

  protected Entity parent;

  protected final Set<EntityTreeNode> children;

  public EntityTreeNode(Entity entity, Entity parent) {
    this.entity = entity;
    this.parent = parent;
    this.children = new HashSet<>();
  }

  public EntityTreeNode() {
    this(null, null);
  }

  public boolean hasChild(EntityTreeNode node) {
    return children.contains(node);
  }

  public void addChild(EntityTreeNode node) {
    children.add(node);
    node.parent = entity;
  }

  public void removeChild(EntityTreeNode node) {
    node.parent = null;
    children.remove(node);
  }

  public void walkDown(Consumer<EntityTreeNode> walker) {
    walkDown(walker, false);
  }

  public void walkDown(Consumer<EntityTreeNode> walker, boolean skipThis) {
    walkDown(walker, skipThis, null);
  }

  public void walkDown(Consumer<EntityTreeNode> beforeWalk, boolean skipThis, Consumer<EntityTreeNode> afterWalk) {
    if(!skipThis) {
      beforeWalk.accept(this);
    }
    children.forEach(child -> child.walkDown(beforeWalk, false, afterWalk));
    if(afterWalk != null) {
      afterWalk.accept(this);
    }
  }

  public EntityTemplate toTemplate() {
    return new EntityTemplate().copyFromNode(this);
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }
    if (!(obj instanceof EntityTreeNode)) {
      return false;
    }

    EntityTreeNode that = (EntityTreeNode)obj;
    return entity.getId().equals(that.entity.getId());
  }

  @Override
  public int compareTo(EntityTreeNode o) {
    return entity.getId().compareTo(o.entity.getId());
  }

  @Override
  public int hashCode() {
    return entity.getId().hashCode();
  }

}
