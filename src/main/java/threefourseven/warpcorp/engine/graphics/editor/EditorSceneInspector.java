package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import threefourseven.warpcorp.engine.asset.Mesh;
import threefourseven.warpcorp.engine.entity.Entity;
import threefourseven.warpcorp.engine.entity.component.*;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiAssetInput;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiBlock;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiUtil;

import java.util.Collection;

public class EditorSceneInspector extends ImGuiBlock {

  private final EditorSceneTree tree;

  private final ImGuiAssetInput meshAssetInput = new ImGuiAssetInput(Mesh.class);

  public EditorSceneInspector(EditorSceneTree tree) {
    this.tree = tree;
  }

  private void renderNameIfPresent(Collection<Component> components) {
    for(Component component : components) {
      if(component instanceof NameComponent) {
        ImGuiUtil.nameComponent((NameComponent)component);
      }
    }
  }

  private void renderTransformIfPresent(Entity entity, Collection<Component> components) {
    for(Component component : components) {
      if(component instanceof TransformComponent) {
        ImGuiUtil.transformComponent(entity, (TransformComponent)component);
      }
    }
  }

  private void renderMeshIfPresent(Window window, Collection<Component> components) {
    for(Component component : components) {
      if(component instanceof MeshComponent) {
        ImGuiUtil.meshComponent(window, (MeshComponent)component);
      }
    }
  }

  private void renderMaterialIfPresent(Window window, Collection<Component> components) {
    for(Component component : components) {
      if(component instanceof MaterialComponent) {
        ImGuiUtil.materialComponent(window, (MaterialComponent)component);
      }
    }
  }

  private void renderSpriteLayerIfPresent(Collection<Component> components) {
    for(Component component : components) {
      if(component instanceof SpriteLayerComponent) {
        ImGuiUtil.spriteLayerComponent((SpriteLayerComponent)component);
      }
    }
  }

  private void renderAnimatedSpriteIfPresent(Collection<Component> components) {
    for(Component component : components) {
      if(component instanceof AnimatedSpriteComponent) {
        ImGuiUtil.animatedSpriteComponent((AnimatedSpriteComponent)component);
      }
    }
  }

  private void renderEntityInspector(Window window, Entity entity) {
    Collection<Component> components = entity.getComponents().values();
    renderNameIfPresent(components);
    renderTransformIfPresent(entity, components);
    renderMeshIfPresent(window, components);
    renderMaterialIfPresent(window, components);
    renderSpriteLayerIfPresent(components);
    renderAnimatedSpriteIfPresent(components);
  }

  @Override
  public void onOpen(Window window, Object... properties) {
    meshAssetInput.onOpen(window, properties);
  }

  @Override
  public void onRender(Window window) {
    if(tree.getSelected().size() == 0) {
      ImGui.text("Nothing is selected");
    } else {
      for(Entity entity : tree.getSelected()) {
        renderEntityInspector(window, entity);
      }
    }
  }

  @Override
  public void onClose(Window window) {
    meshAssetInput.onClose(window);
  }

}
