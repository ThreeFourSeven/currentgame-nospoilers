package threefourseven.warpcorp.engine.graphics.shader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import threefourseven.warpcorp.engine.asset.ShaderScript;

@Getter
@AllArgsConstructor
public class ShaderProgram {

  protected final ShaderScript shaderScript;
  protected final int id;

}
