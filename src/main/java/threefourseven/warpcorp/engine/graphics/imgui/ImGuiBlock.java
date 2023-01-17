package threefourseven.warpcorp.engine.graphics.imgui;

import threefourseven.warpcorp.engine.util.AutoIdentified;

public abstract class ImGuiBlock extends AutoIdentified implements ImGuiComponent {

  protected boolean visible = true;

  @Override
  public void toggleVisible() {
    visible = !visible;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }
}
