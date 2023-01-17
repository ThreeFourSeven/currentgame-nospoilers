package threefourseven.warpcorp.engine.graphics;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

@Getter
public class Window {
  protected String title;
  @Setter
  protected int width;
  @Setter
  protected int height;
  protected long glfwId;

  public Window(String title, int width, int height, long glfwId) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.glfwId = glfwId;
  }

  public void swapFrame() {
    GLFW.glfwSwapBuffers(glfwId);
  }

  public boolean shouldClose() {
    return GLFW.glfwWindowShouldClose(glfwId);
  }

  public void destroy() {
    GLFW.glfwDestroyWindow(glfwId);
  }

}
