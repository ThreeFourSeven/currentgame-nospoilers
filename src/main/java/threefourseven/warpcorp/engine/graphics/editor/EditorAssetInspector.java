package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import org.joml.Vector2f;
import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.graphics.Graphics;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiBlock;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiUtil;

import java.util.Optional;

public class EditorAssetInspector extends ImGuiBlock {

  @Override
  public void onOpen(Window window, Object... properties) {
  }

  @Override
  public void onRender(Window window) {
    if(AssetUtil.getSelectedAssets().size() == 0) {
      ImGui.text("No Asset is selected");
    } else {
      AssetUtil.getSelectedAssets().stream()
        .map(AssetUtil.getStore()::get)
        .findFirst().orElse(Optional.empty())
        .ifPresent(asset -> renderAsset(window, asset));
    }
  }

  @Override
  public void onClose(Window window) {
  }

  private void renderAsset(Window window, Asset asset) {
    asset.get().ifPresent(data -> {
      if(data instanceof String) {
        ImGui.textWrapped(data.toString());
      } else if(data instanceof Image) {
        float width = ImGui.getContentRegionAvailX();
        ImGui.image(Graphics.createTexture((Image)data).getId(), width,  Math.min(width, ImGui.getContentRegionAvailY()));
      } else if(data instanceof Mesh) {
        ImGuiUtil.mesh("Mesh", (Mesh)data);
      } else if(data instanceof ShaderScript) {
        ImGuiUtil.shaderScript("Shader", (ShaderScript)data);
      } else if(data instanceof Material) {
        ImGuiUtil.material(window, "Material", (Material)data);
      } else if(data instanceof SpriteSheet) {
        ImGuiUtil.spriteSheet(window, "SpriteSheet", (SpriteSheet)data);
      }
    });
  }

}
