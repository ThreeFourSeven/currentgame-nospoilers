package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.graphics.Graphics;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiUtil;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiWindow;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class EditorSpriteRegistry extends ImGuiWindow {

  private String spriteSearchFilter = "";
  private String spriteSheetSearchFilter = "";

  private String selectedSprite = "";

  private String selectedSpriteSheet = "";

  private String newSpriteName = "default";
  private final SpriteData newSpriteData = new SpriteData("default_atlas", 0, 0, 1, 1);

  public EditorSpriteRegistry() {
    super("SpriteRegistry");
  }

  @Override
  public void onOpen(Window window, Object... properties) {

  }

  @Override
  public void onRender(Window window) {
    if(ImGui.collapsingHeader("Sprites")) {
      if(ImGui.button("Create Sprite")) {
        ImGui.openPopup("CreateSpritePopup");
      }
      ImGui.sameLine();
      spriteSearchFilter = ImGuiUtil.textInput("Sprite Search:", spriteSearchFilter, 256);
      ImGui.separator();
      ImGui.columns(2);
      Graphics.getSpriteRegistry().getSprites()
        .keySet().stream().filter(name -> name.toLowerCase().contains(spriteSearchFilter.toLowerCase()))
        .forEach(spriteName -> {
          if(ImGui.selectable(String.format("%s##Sprite", spriteName), selectedSprite.equals(spriteName))) {
            if(selectedSprite.equals(spriteName)) {
              selectedSprite = "";
            } else {
              selectedSprite = spriteName;
            }
          }
        });
      ImGui.nextColumn();
      if(selectedSprite.length() > 0) {
        Graphics.getSpriteRegistry().getSpriteData(selectedSprite).ifPresent(data -> ImGuiUtil.spriteData(selectedSprite + " SpriteData", data));
      } else {
        ImGui.text("No Sprite Selected");
      }
      ImGui.columns(1);
      ImGui.separator();
    }

    if(ImGui.collapsingHeader("SpriteSheets")) {
      if(ImGui.button("Create SpriteSheet")) {
        AssetUtil.getSelectedAssets().stream().map(AssetUtil.getStore()::get)
          .filter(Optional::isPresent).map(Optional::get).filter(asset -> asset.getDataClass().equals(Image.class))
          .findFirst().ifPresent(
            asset -> {
              Path imagePath = Paths.get(asset.getLocation().getPath());
              String imageFileName = imagePath.getFileName().toString();
              String fileName = imageFileName.substring(0, imageFileName.lastIndexOf("."));
              String spriteSheetPath = asset.getLocation().getPath().replace(imagePath.getFileName().toString(), String.format("%s.wcss", fileName));
              new Asset(AssetLocation.absolute(spriteSheetPath), SpriteSheet.class)
                .set(new SpriteSheet(asset))
                .get().map(data -> (SpriteSheet)data)
                .ifPresent(spriteSheet -> Graphics.getSpriteRegistry().register(fileName, spriteSheet));
            }
          );

      }
      ImGui.sameLine();
      spriteSheetSearchFilter = ImGuiUtil.textInput("SpriteSheet Search:", spriteSheetSearchFilter, 256);
      ImGui.separator();
      ImGui.columns(2);
      Graphics.getSpriteRegistry().getSpriteSheets()
        .keySet().stream().filter(name -> name.toLowerCase().contains(spriteSheetSearchFilter.toLowerCase()))
        .forEach(spriteSheetName -> {
          if(ImGui.selectable(String.format("%s##SpriteSheet", spriteSheetName), selectedSpriteSheet.equals(spriteSheetName))) {
            if(selectedSpriteSheet.equals(spriteSheetName)) {
              selectedSpriteSheet = "";
            } else {
              selectedSpriteSheet = spriteSheetName;
            }
          }
        });
      ImGui.nextColumn();
      if(selectedSpriteSheet.length() > 0) {
        Graphics.getSpriteRegistry().getSpriteSheet(selectedSpriteSheet).ifPresent(spriteSheet -> ImGuiUtil.spriteSheet(window, selectedSpriteSheet + " SpriteSheet", spriteSheet));
      } else {
        ImGui.text("No SpriteSheet Selected");
      }
      ImGui.columns(1);
    }

    if(ImGui.beginPopup("CreateSpritePopup")) {
      newSpriteName = ImGuiUtil.textInput("Sprite Name", newSpriteName, 256);
      ImGuiUtil.spriteData("New Sprite Data", newSpriteData);
      if(ImGui.button("Create")) {
        Graphics.getSpriteRegistry().register(newSpriteName, newSpriteData);
        ImGui.closeCurrentPopup();
      }
      ImGui.endPopup();
    }
  }

  @Override
  public void onClose(Window window) {
  }

}
