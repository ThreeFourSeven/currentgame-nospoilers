package threefourseven.warpcorp.engine.graphics.imgui;

import imgui.ImGui;
import lombok.Setter;
import threefourseven.warpcorp.engine.asset.Asset;
import threefourseven.warpcorp.engine.asset.AssetUtil;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.input.Input;

import java.util.Optional;

@Setter
public class ImGuiAssetInput extends ImGuiBlock {

  protected final ImGuiContextMenu clickContextMenu = new ImGuiContextMenu();

  protected final Class<?> dataClass;
  protected Asset asset;

  public ImGuiAssetInput(Class<?> dataClass, Asset asset) {
    this.dataClass = dataClass;
    this.asset = asset;
  }

  public ImGuiAssetInput(Class<?> dataClass) {
    this(dataClass, null);
  }

  @Override
  public void onOpen(Window window, Object... properties) {
    clickContextMenu.onOpen(window, "ImGuiAssetInputOptions",
      new ImGuiContextItem(
        "Use Selected",
        option -> {
          Optional<Asset> op = AssetUtil.getSelectedAssets().stream().map(AssetUtil.getStore()::get)
            .filter(Optional::isPresent).map(Optional::get).filter(asset -> asset.getDataClass().equals(dataClass)).findFirst();
          op.ifPresent(asset -> {
            this.asset = asset;
            if(!Input.key.isDown("left_control") && !Input.key.isDown("right_control")) {
              AssetUtil.toggleSelection(asset);
            }
          });
        },
        () -> AssetUtil.getSelectedAssets().stream()
          .map(AssetUtil.getStore()::get).filter(Optional::isPresent)
          .map(Optional::get).noneMatch(asset -> asset.getDataClass().equals(dataClass))
      ),
      new ImGuiContextItem(
        "Remove",
        option -> asset = null,
        () -> asset == null
      )
    );
  }

  @Override
  public void onRender(Window window) {
    if(ImGui.button(String.format("Asset: %s##%s", dataClass.getSimpleName(), id))) {
      clickContextMenu.show();
    }

    clickContextMenu.onRender(window);
  }

  @Override
  public void onClose(Window window) {

  }

  public Optional<Asset> getAsset() {
    if(asset != null)
      return Optional.of(asset);
    return Optional.empty();
  }

}
