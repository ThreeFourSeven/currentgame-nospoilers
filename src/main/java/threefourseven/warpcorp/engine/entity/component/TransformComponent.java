package threefourseven.warpcorp.engine.entity.component;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import threefourseven.warpcorp.engine.util.AutoIdentified;

public class TransformComponent extends AutoIdentified implements Component {

  public static final int matrixSize = 16;

  @Getter
  protected Vector3f position;

  @Getter
  protected Vector3f scale;

  @Getter
  protected Vector3f rotation;

  @Getter
  protected final float[] relativeMatrix = new float[matrixSize];

  @Getter
  protected final float[] absoluteMatrix = new float[matrixSize];

  public TransformComponent(Vector3f position, Vector3f scale, Vector3f rotation) {
    this.position = position;
    this.scale = scale;
    this.rotation = rotation;
    updateRelativeMatrix();
    updateAbsoluteMatrix(null);
  }

  public TransformComponent() {
    this(new Vector3f(), new Vector3f(1, 1, 1), new Vector3f());
    updateRelativeMatrix();
    updateAbsoluteMatrix(null);
  }

  public void setPosition(float x, float y, float z) {
    position.set(x, y, z);
  }

  public void translate(float x, float y, float z) {
    position.add(x, y, z);
  }

  public void setScale(float x, float y, float z) {
    scale.set(x, y, z);
  }

  public void scale(float x, float y, float z) {
    scale.mul(x, y, z);
  }

  public void setRotation(float x, float y, float z) {
    rotation.set(x, y, z);
  }

  public void rotate(float x, float y, float z) {
    rotation.add(x, y, z);
  }

  public void updateRelativeMatrix() {
    Matrix4f m = new Matrix4f();
    m.identity();
    m.translate(position);
    m.rotate(new Quaternionf().rotateXYZ((float)Math.toRadians(rotation.x), (float)Math.toRadians(rotation.y), (float)Math.toRadians(rotation.z)));
    m.scale(scale);
    m.get(relativeMatrix);
  }

  public void updateAbsoluteMatrix(float[] parentMatrix) {
    if(parentMatrix == null) {
      System.arraycopy(relativeMatrix, 0, absoluteMatrix, 0, absoluteMatrix.length);
    } else {
      Matrix4f m = new Matrix4f();
      m.set(parentMatrix);
      m.mul(new Matrix4f().set(relativeMatrix));
      m.get(absoluteMatrix);
    }
  }

  @Override
  public TransformComponent copy() {
    return new TransformComponent(new Vector3f(position), new Vector3f(scale), new Vector3f(rotation));
  }

}
