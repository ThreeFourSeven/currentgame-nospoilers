package threefourseven.warpcorp.engine.graphics.shader.shaderuniform;

public interface ShaderUniformLoader<T> {
  void load(int location, T value);
}
