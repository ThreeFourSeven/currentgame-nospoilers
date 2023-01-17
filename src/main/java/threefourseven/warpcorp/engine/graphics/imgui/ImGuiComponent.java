package threefourseven.warpcorp.engine.graphics.imgui;

import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.util.Identified;

public interface ImGuiComponent extends Identified {
  void onOpen(Window window, Object... properties);
  void onRender(Window window);
  void onClose(Window window);
  boolean isVisible();
  void toggleVisible();
}
