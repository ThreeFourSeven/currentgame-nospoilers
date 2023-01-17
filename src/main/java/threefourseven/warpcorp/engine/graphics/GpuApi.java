package threefourseven.warpcorp.engine.graphics;

import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiContext;
import threefourseven.warpcorp.engine.graphics.shader.ShaderProgram;
import threefourseven.warpcorp.engine.graphics.shader.shaderuniform.ShaderUniform;
import threefourseven.warpcorp.engine.graphics.texture.Texture;

import java.util.stream.Stream;

public interface GpuApi {

  void initialize(Window window);

  void initializeImGui(Window window);

  void destroy();

  void clearFrame();

  void viewport(int width, int height);

  Texture createTexture(Image image);

  void destroyTexture(Texture texture);

  void useTexture(Texture texture);

  ShaderProgram createShaderProgram(ShaderScript shaderScript);

  void useShaderProgram(ShaderProgram shaderProgram);

  void destroyShaderProgram(ShaderProgram shaderProgram);

  void renderInstances(View view, Mesh mesh, Material material, Stream<TransformComponent> transforms);

  void renderSprites(SpriteRegistry registry, View view, Stream<Sprite> spriteStream);

  <T> void loadShaderUniform(ShaderProgram shaderProgram, ShaderUniform<T> shaderUniform);

  ImGuiContext getImGuiContext();

}
