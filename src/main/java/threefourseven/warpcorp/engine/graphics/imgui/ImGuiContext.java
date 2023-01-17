package threefourseven.warpcorp.engine.graphics.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.logger.Logger;

import java.util.*;

public class ImGuiContext {

  private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
  private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

  private final Set<String> componentsToRemove = new HashSet<>();
  private final Map<String, ImGuiComponent> components = new HashMap<>();
  private final Window window;

  public ImGuiContext(Window window) {
    this.window = window;
  }

  public void initialize() {
    imGuiGlfw.init(window.getGlfwId(), true);
    imGuiGl3.init();
  }

  public void addComponent(ImGuiComponent component, Object... properties) {
    Logger.debug(String.format("Adding %s Component to ImGuiContext", component.getClass().getSimpleName()));
    component.onOpen(window);
    components.put(component.getId(), component);
  }

  public void removeComponent(ImGuiComponent component) {
    Logger.debug(String.format("Removing %s Component from ImGuiContext", component.getClass().getSimpleName()));
    componentsToRemove.add(component.getId());
  }

  public void swapFrame() {
    imGuiGlfw.newFrame();
    ImGui.newFrame();

    for(ImGuiComponent component : components.values()) {
      if(component instanceof ImGuiWindow) {
        ImGuiWindow imGuiWindow = (ImGuiWindow)component;
        if(imGuiWindow.begin()) {
          component.onRender(window);
          imGuiWindow.end();
        }
      } else {
        component.onRender(window);
      }
    }
    for(String id : componentsToRemove) {
      components.get(id).onClose(window);
      components.remove(id);
    }
    componentsToRemove.clear();
    ImGui.render();
    imGuiGl3.renderDrawData(ImGui.getDrawData());
    if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
      final long backupWindowPtr = GLFW.glfwGetCurrentContext();
      ImGui.updatePlatformWindows();
      ImGui.renderPlatformWindowsDefault();
      GLFW.glfwMakeContextCurrent(backupWindowPtr);
    }
  }

}
