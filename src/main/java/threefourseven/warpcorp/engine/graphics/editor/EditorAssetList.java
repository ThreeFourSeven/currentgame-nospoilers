package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import imgui.type.ImString;
import threefourseven.warpcorp.engine.asset.Asset;
import threefourseven.warpcorp.engine.asset.AssetUtil;
import threefourseven.warpcorp.engine.command.Command;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiBlock;
import threefourseven.warpcorp.engine.logger.Logger;

import javax.swing.*;
import java.io.File;
import java.util.Map;

public class EditorAssetList extends ImGuiBlock {

  private final ImString searchText = new ImString(512);

  private JFileChooser chooser;

  @Override
  public void onOpen(Window window, Object... properties) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      chooser = new JFileChooser();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.warning(e.getLocalizedMessage());
    }
  }

  @Override
  public void onRender(Window window) {
    if(ImGui.button("Create")) {
      chooser.setCurrentDirectory(new File("."));
      int result = chooser.showOpenDialog(null);
      if(result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        Command.runCommand("create_asset " + file.getPath());
      }
    }
    ImGui.sameLine();
    if(ImGui.button("Remove")) {
      for(String path : AssetUtil.getSelectedAssets()) {
        Command.runCommand("remove_asset " + path);
      }
      AssetUtil.getSelectedAssets().clear();
    }
    ImGui.sameLine();
    ImGui.text("Search:");
    ImGui.sameLine();
    ImGui.inputText("##Search", searchText);
    String pathFilter = searchText.get();

    float buttonWidth = ImGui.getContentRegionAvailX();

    Map<String, Asset> assets = AssetUtil.getStore().getAssets();
    for(String path : assets.keySet()) {
      if(path.toLowerCase().contains(pathFilter.toLowerCase())) {
        Asset asset = assets.get(path);
        ImGui.alignTextToFramePadding();
        if(ImGui.selectable(path, AssetUtil.getSelectedAssets().contains(path), 0, buttonWidth, 48f)) {
          AssetUtil.toggleSelection(asset);
        }
      }
    }
  }

  @Override
  public void onClose(Window window) {

  }

}
