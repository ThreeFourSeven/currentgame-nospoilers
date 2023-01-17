package threefourseven.warpcorp.engine.graphics.imgui;

import imgui.ImGui;
import imgui.type.ImBoolean;
import threefourseven.warpcorp.engine.util.AutoIdentified;

public abstract class ImGuiWindow extends AutoIdentified implements ImGuiComponent {

  protected final String title;

  protected boolean visible = false;

  public ImGuiWindow(String title, boolean visible) {
    this.visible = visible;
    this.title = title;
  }

  public ImGuiWindow(String title) {
    this(title,false);
  }

  public boolean begin() {
    return ImGui.begin(title, new ImBoolean(visible));
  }

  public void end() {
    ImGui.end();
  }

  @Override
  public void toggleVisible() {
    visible = !visible;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }
}
