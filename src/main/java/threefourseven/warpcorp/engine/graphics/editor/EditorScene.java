package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import org.joml.Vector3f;
import threefourseven.warpcorp.WarpCorp;
import threefourseven.warpcorp.engine.command.Command;
import threefourseven.warpcorp.engine.entity.EntityTemplate;
import threefourseven.warpcorp.engine.entity.EntityUtil;
import threefourseven.warpcorp.engine.entity.Scene;
import threefourseven.warpcorp.engine.entity.SceneUtil;
import threefourseven.warpcorp.engine.entity.component.MaterialComponent;
import threefourseven.warpcorp.engine.entity.component.MeshComponent;
import threefourseven.warpcorp.engine.entity.component.NameComponent;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;
import threefourseven.warpcorp.engine.graphics.Graphics;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiContextItem;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiContextItemMenu;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiContextMenu;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiWindow;

import java.util.List;

public class EditorScene extends ImGuiWindow {

  private final EditorSceneTree tree = new EditorSceneTree();
  private final EditorSceneInspector inspector = new EditorSceneInspector(tree);
  private final ImGuiContextMenu rightClickContext = new ImGuiContextMenu();

  private EntityTemplate entityCopy = null;

  public EditorScene() {
    super("SceneEditor", WarpCorp.isDevMode());
  }

  @Override
  public void onOpen(Window window, Object... properties) {
    tree.onOpen(window, properties);
    inspector.onOpen(window, properties);
    rightClickContext.onOpen(window, "EntityCreatePopup",
      new ImGuiContextItemMenu("Create", List.of(
        new ImGuiContextItem(
          "Quad",
          option -> tree.getSelected().stream().findFirst()
            .ifPresentOrElse(
              parent -> SceneUtil.generateEntity(parent, "Quad"),
              () -> SceneUtil.generateEntity("Quad")
            )
        ),
        new ImGuiContextItem(
          "Triangle",
          option -> tree.getSelected().stream().findFirst()
            .ifPresentOrElse(
              parent -> SceneUtil.generateEntity(parent, "Triangle"),
              () -> SceneUtil.generateEntity("Triangle")
            )
        ),
        new ImGuiContextItem(
          "SpriteLayer",
          option -> tree.getSelected().stream().findFirst()
            .ifPresentOrElse(
              parent -> SceneUtil.generateEntity(parent, "SpriteLayer"),
              () -> SceneUtil.generateEntity("SpriteLayer")
            )
        ),
        new ImGuiContextItem(
          "AnimatedSprite",
          option -> tree.getSelected().stream().findFirst()
            .ifPresentOrElse(
              parent -> SceneUtil.generateEntity(parent, "AnimatedSprite"),
              () -> SceneUtil.generateEntity("AnimatedSprite")
            )
        )
      )),
      new ImGuiContextItem(
        "Copy",
        option -> tree.getSelected().stream().findFirst()
          .ifPresent(target -> {
            tree.deselect(target);
            SceneUtil.getScene().getEntityTree().getNode(target).ifPresent(node -> entityCopy = node.toTemplate());
          }),
        () -> tree.getSelected().size() == 0
      ),
      new ImGuiContextItem(
        "Paste",
        option -> {
          tree.getSelected().stream().findFirst().ifPresentOrElse(
            parent -> SceneUtil.getScene().createEntityFromTemplate(parent, entityCopy),
            () -> SceneUtil.getScene().createEntityFromTemplate(entityCopy)
          );
          entityCopy = null;
        },
        () -> entityCopy == null
      ),
      new ImGuiContextItem(
        "Remove",
        option -> tree.getSelected().stream().findFirst()
          .ifPresent(target -> {
            tree.deselect(target);
            Command.runCommand("remove_entity " + target.getId());
          }),
        () -> tree.getSelected().size() == 0
      ),
      new ImGuiContextItem(
        "Remove All Children",
        option -> tree.getSelected().stream().findFirst()
          .ifPresent(target -> {
            tree.deselect(target);
            Command.runCommand("remove_all_children " + target.getId());
          }),
        () -> tree.getSelected().size() == 0
      )
    );
  }

  @Override
  public void onRender(Window window) {
    ImGui.columns(2);

    ImGui.beginChild("Scene");
    tree.onRender(window);
    ImGui.endChild();

    if(ImGui.isItemHovered() && ImGui.isMouseClicked(1)) {
      rightClickContext.show();
    }

    rightClickContext.onRender(window);

    ImGui.nextColumn();

    ImGui.beginChild("Inspector");
    inspector.onRender(window);
    ImGui.endChild();

    ImGui.columns(1);
  }

  @Override
  public void onClose(Window window) {

  }
}
