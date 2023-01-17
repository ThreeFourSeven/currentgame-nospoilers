package threefourseven.warpcorp.engine.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import threefourseven.warpcorp.engine.util.AutoIdentified;

@Getter
@AllArgsConstructor
public class Mesh extends AutoIdentified {

  private final int vertexCount;
  private final int indexCount;
  private final int vertexSize;
  private final int vertexSegmentCount;
  private final int[] vertexSegmentSizes;
  private final float[] vertexData;
  private final int[] indexData;

  public static int calculateTotalSizeInBytes(Mesh mesh) {
    return Integer.BYTES * (4 + mesh.getIndexCount() + mesh.getVertexSegmentCount()) + Float.BYTES * mesh.getVertexSize() * mesh.getVertexCount();
  }

}
