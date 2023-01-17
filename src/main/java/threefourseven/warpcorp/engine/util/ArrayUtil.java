package threefourseven.warpcorp.engine.util;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ArrayUtil {

  public static ByteBuffer arrayToBuffer(byte[] array) {
    ByteBuffer buffer = BufferUtils.createByteBuffer(array.length);
    for(byte b : array) {
      buffer.put(b);
    }
    return buffer;
  }

  public static IntBuffer arrayToBuffer(int[] array) {
    IntBuffer buffer = BufferUtils.createIntBuffer(array.length);
    for(int b : array) {
      buffer.put(b);
    }
    return buffer;
  }

  public static FloatBuffer arrayToBuffer(float[] array) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length);
    for(float b : array) {
      buffer.put(b);
    }
    return buffer;
  }

}
