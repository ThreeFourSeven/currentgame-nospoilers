package threefourseven.warpcorp.engine.graphics.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import imgui.type.ImBoolean;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.util.AutoIdentified;
import threefourseven.warpcorp.engine.util.Named;

import java.util.ArrayList;
import java.util.List;

public class ImGuiContextMenu extends AutoIdentified implements ImGuiComponent {

  protected boolean visible = false;

  protected String name = "Default";
  protected List<Named> options = new ArrayList<>();

  public void show() {
    ImGui.openPopup(name);
  }

  @Override
  public void onOpen(Window window, Object... properties) {
    for(Object object : properties) {
      if(object instanceof String) {
        name = object.toString();
      } else if(object instanceof ImGuiContextItem) {
        options.add((ImGuiContextItem)object);
      } else if(object instanceof ImGuiContextItemMenu) {
        options.add((ImGuiContextItemMenu)object);
      }
    }
  }

  @Override
  public void onRender(Window window) {
    if(ImGui.beginPopup(name)) {
      if(options.size() == 0) {
        ImGui.closeCurrentPopup();
      }
      renderOptions(options, true);
      ImGui.endPopup();
    }
  }

  @Override
  public void onClose(Window window) {

  }

  @Override
  public void toggleVisible() {
    visible = !visible;
    if(visible) {
      ImGui.openPopup(name);
    }
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  private void renderOptions(List<Named> options, boolean root) {
    for(Named option : options) {
      if(option instanceof ImGuiContextItem) {
        ImGuiContextItem imGuiItem = (ImGuiContextItem)option;
        if(root) {
          if(ImGui.selectable(option.getName(), new ImBoolean(), imGuiItem.shouldDisable.get() ? ImGuiSelectableFlags.Disabled : 0)) {
            imGuiItem.onSelect.accept(option.getName());
            ImGui.closeCurrentPopup();
          }
        } else {
          if(ImGui.menuItem(option.getName())) {
            imGuiItem.onSelect.accept(option.getName());
            ImGui.closeCurrentPopup();
          }
        }
      } else if(option instanceof ImGuiContextItemMenu) {
        ImGuiContextItemMenu imGuiItemMenu = (ImGuiContextItemMenu)option;
        if(ImGui.beginMenu(option.getName())) {
          renderOptions(imGuiItemMenu.getOptions(), false);
          ImGui.endMenu();
        }
      }
    }
  }
}
