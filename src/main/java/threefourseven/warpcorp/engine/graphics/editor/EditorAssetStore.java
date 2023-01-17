package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiWindow;

public class EditorAssetStore extends ImGuiWindow {

  private final EditorAssetList list = new EditorAssetList();
  private final EditorAssetInspector inspector = new EditorAssetInspector();

  public EditorAssetStore() {
    super("AssetStore");
  }

  @Override
  public void onOpen(Window window, Object... properties) {
    list.onOpen(window, properties);
    inspector.onOpen(window, properties);
  }

  @Override
  public void onRender(Window window) {
    ImGui.columns(2);

    ImGui.beginChild("AssetList");
    list.onRender(window);
    ImGui.endChild();

    ImGui.nextColumn();

    ImGui.beginChild("AssetInspector");
    inspector.onRender(window);
    ImGui.endChild();

    ImGui.columns(1);
  }

  @Override
  public void onClose(Window window) {
    list.onClose(window);
    inspector.onClose(window);
  }

}
