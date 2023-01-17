package threefourseven.warpcorp.engine.graphics.shader.shaderuniform;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ShaderUniform<T> {

  private String name;


  @Setter
  private T value;

  public ShaderUniform(String name, T value) {
    this.name = name;
    this.value = value;
  }

  public ShaderUniform() {
    this("", null);
  }

}
