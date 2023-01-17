package threefourseven.warpcorp.engine.graphics;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import threefourseven.warpcorp.engine.graphics.shader.shaderuniform.ShaderUniform;

@Getter
public class View {

  protected final Vector3f position;

  protected final ShaderUniform<Matrix4f> viewProjectionUniform = new ShaderUniform<>("viewProjection", new Matrix4f().identity());

  public View(Vector3f position) {
    this.position = position;
  }

  public void update(Window window) {
    viewProjectionUniform.setValue(new Matrix4f()
      .identity()
      .perspective((float)Math.toRadians(90), (float)window.getWidth() / (float)window.getHeight(), 0.1f, 100f)
      .mul(
        new Matrix4f()
          .identity()
          .lookAt(position, new Vector3f(0, 0, -1).add(position), new Vector3f(0, 1, 0))
      )
    );
  }

}
