package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import imgui.type.ImBoolean;
import lombok.Getter;
import org.joml.Vector3f;
import threefourseven.warpcorp.engine.asset.Asset;
import threefourseven.warpcorp.engine.asset.AssetLocation;
import threefourseven.warpcorp.engine.asset.Mesh;
import threefourseven.warpcorp.engine.asset.ShaderScript;
import threefourseven.warpcorp.engine.entity.Entity;
import threefourseven.warpcorp.engine.entity.EntityTreeNode;
import threefourseven.warpcorp.engine.entity.EntityUtil;
import threefourseven.warpcorp.engine.entity.SceneUtil;
import threefourseven.warpcorp.engine.entity.component.MaterialComponent;
import threefourseven.warpcorp.engine.entity.component.MeshComponent;
import threefourseven.warpcorp.engine.entity.component.NameComponent;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;
import threefourseven.warpcorp.engine.graphics.Graphics;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiBlock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class EditorSceneTree extends ImGuiBlock {

  private Set<Entity> selected = new HashSet<>();

  public void select(Entity entity) {
    selected.add(entity);
  }

  public void deselect(Entity entity) {
    selected.remove(entity);
  }

  public void setSelected(Entity entity, boolean selection) {
    if(selection) {
      select(entity);
    } else {
      deselect(entity);
    }
  }
  public void toggleSelection(Entity entity, boolean clearSelections) {
    boolean selection = !selected.contains(entity);
    selected.clear();
    setSelected(entity, selection);
  }

  private void renderTree(EntityTreeNode node) {
    String label = String.format("%s##%s", node.getEntity().getName().orElse(node.getEntity().getId()), node.getEntity().getId());
    if(node.getChildren().size() == 0) {
      if(ImGui.selectable(label, new ImBoolean(selected.contains(node.getEntity())))) {
        toggleSelection(node.getEntity(), true);
      }
    } else {
      boolean open = ImGui.treeNode("##"+node.getEntity().getId());
      ImGui.sameLine();
      if(ImGui.selectable(label, new ImBoolean(selected.contains(node.getEntity())))) {
        toggleSelection(node.getEntity(), true);
      }
      if(open) {
        for(EntityTreeNode child : node.getChildren()) {
          renderTree(child);
        }
        ImGui.treePop();
      }
    }
  }

  @Override
  public void onOpen(Window window, Object... properties) {
  }

  @Override
  public void onRender(Window window) {
    ImGui.text(String.format("Entity Count: %d", SceneUtil.getEntityCount()));
    ImGui.text(String.format("View Position: %.3f, %.3f, %.3f", Graphics.getView().getPosition().x, Graphics.getView().getPosition().y, Graphics.getView().getPosition().z));
    ImGui.separator();
    renderTree(SceneUtil.getScene().getEntityTree().getRoot());
  }

  @Override
  public void onClose(Window window) {

  }
}
