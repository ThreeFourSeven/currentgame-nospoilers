package threefourseven.warpcorp.engine.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import threefourseven.warpcorp.WarpCorp;
import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.logger.Logger;
import threefourseven.warpcorp.engine.util.ArrayUtil;
import threefourseven.warpcorp.engine.util.ObjectReader;
import threefourseven.warpcorp.engine.util.ObjectWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class IO {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final Map<String, ObjectReader> readers = new HashMap<>();

  private static final Map<String, ObjectWriter> writers = new HashMap<>();

  public static void initialize() {
    addObjectReader(IO::readObjectAsString, String.class);
    addObjectReader(IO::readObjectAsImage, Image.class);
    addObjectReader(IO::readObjectAsShaderScript, ShaderScript.class);
    addObjectReader(IO::readObjectAsMesh, Mesh.class);
    addObjectReader(IO::readObjectAsJson, JsonNode.class);

    addObjectWriter(IO::writeObjectAsString, String.class);
    addObjectWriter(IO::writeObjectAsImage, Image.class);
    addObjectWriter(IO::writeObjectAsShaderScript, ShaderScript.class);
    addObjectWriter(IO::writeObjectAsMesh, Mesh.class);
    addObjectWriter(IO::writeObjectAsJson, JsonNode.class);
  }

  public static <T> Optional<T> readFileAs(String path, Class<T> tClass) {
    String typeName = tClass.getTypeName();
    Optional<InputStream> isOp = getFileInputStream(path);
    if(isOp.isPresent()) {
      if(readers.containsKey(typeName)) {
        return readers.get(typeName).read(path, isOp.get()).map(o -> (T)o);
      } else {
        Optional<JsonNode> jsonOp = readObjectAsJson(path, isOp.get()).map(o -> (JsonNode)o);
        if(jsonOp.isPresent()) {
          try {
            T value = objectMapper.treeToValue(jsonOp.get(), tClass);
            if(value != null) {
              return Optional.of(value);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return Optional.empty();
  }

  public static <T> void writeToFile(String path, T value) {
    String typeName = value.getClass().getTypeName();
    Optional<OutputStream> osOp = getFileOutputStream(path);
    if(osOp.isPresent()) {
      if(writers.containsKey(typeName)) {
        writers.get(typeName).write(value, osOp.get());
      } else {
        try {
          JsonNode json = objectMapper.valueToTree(value);
          if(WarpCorp.isDevMode()) {
            osOp.get().write(json.toPrettyString().getBytes(StandardCharsets.UTF_8));
          } else {
            osOp.get().write(json.toString().getBytes(StandardCharsets.UTF_8));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static <T> Optional<T> readJarFileAs(String path, Class<T> tClass) {
    String typeName = tClass.getTypeName();
    Optional<InputStream> isOp = getJarFileInputStream(path);
    if(isOp.isPresent()) {
      if(readers.containsKey(typeName)) {
        return readers.get(typeName).read(path, isOp.get()).map(o -> (T)o);
      } else {
        Optional<JsonNode> jsonOp = readObjectAsJson(path, isOp.get()).map(o -> (JsonNode)o);
        if(jsonOp.isPresent()) {
          try {
            T value = objectMapper.treeToValue(jsonOp.get(), tClass);
            if(value != null) {
              return Optional.of(value);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return Optional.empty();
  }

  private static Optional<InputStream> getFileInputStream(String path) {
    try {
      return Optional.of(Files.newInputStream(Paths.get(path)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static Optional<OutputStream> getFileOutputStream(String path) {
    try {
      return Optional.of(Files.newOutputStream(Paths.get(path)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static Optional<InputStream> getJarFileInputStream(String path) {
    try {
      InputStream is = WarpCorp.class.getClassLoader().getResourceAsStream(path);
      if(is != null) {
        return Optional.of(is);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static Optional<Object> readObjectAsString(String path, InputStream is) {
    try {
      String text = new String(is.readAllBytes());
      return Optional.of(text);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static void writeObjectAsString(Object value, OutputStream os) {
    try {
      os.write(value.toString().getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Optional<Object> readObjectAsJson(String path, InputStream is) {
    try {
      JsonNode json = objectMapper.readTree(is);
      if(json != null) {
        return Optional.of(json);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static void writeObjectAsJson(Object value, OutputStream os) {
    try {
      os.write(value.toString().getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Optional<Object> readObjectAsImage(String path, InputStream is) {
    try {
      int[] W = new int[1];
      int[] H = new int[1];
      int[] C = new int[1];
      ByteBuffer bytesBuffer = ArrayUtil.arrayToBuffer(is.readAllBytes());
      bytesBuffer.flip();
      ByteBuffer pixelBytes = STBImage.stbi_load_from_memory(bytesBuffer, W, H, C, 0);
      if(pixelBytes != null) {
        return Optional.of(new Image(W[0], H[0], C[0], pixelBytes));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static Optional<Object> readObjectAsMaterial(String path, InputStream is) {
    Optional<Object> json = readObjectAsJson(path, is);

    if(json.isPresent()) {
      try {
        Material material = objectMapper.treeToValue((JsonNode)json.get(), Material.class);
        material.getShaderScriptAsset().read();
        return Optional.of(material);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return Optional.empty();
  }

  private static Optional<Object> readObjectAsShaderScript(String path, InputStream is) {
    try {
      String text = new String(is.readAllBytes());
      String[] lines = text.split("\n");
      String sectionHeader = "";
      StringBuilder sectionBuilder = new StringBuilder();
      Map<String, String> sourceBlocks = new HashMap<>();
      int i = 0;
      for(String line : lines) {
        if(line.startsWith("@@")) {
          if(sectionHeader.length() > 0) {
            sourceBlocks.put(sectionHeader, sectionBuilder.toString());
            sectionBuilder.setLength(0);
          }
          sectionHeader = line.replace("@@", "").trim();
        } else if(sectionHeader.length() > 0) {
          sectionBuilder.append(line.trim());
          sectionBuilder.append("\n");
          if(i >= lines.length - 1) {
            sourceBlocks.put(sectionHeader, sectionBuilder.toString());
          }
        }
        i++;
      }
      return Optional.of(new ShaderScript(sourceBlocks));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }


  private static Optional<Object> readObjectAsMesh(String path, InputStream is) {
    try {
      ByteBuffer bytesBuffer = ArrayUtil.arrayToBuffer(is.readAllBytes());
      bytesBuffer.position(0);

      int vertexCount = bytesBuffer.getInt();
      int indexCount = bytesBuffer.getInt();
      int vertexSize = bytesBuffer.getInt();
      int vertexSegmentCount = bytesBuffer.getInt();

      int[] vertexSegmentSizes = new int[vertexSegmentCount];
      for(int i = 0; i < vertexSegmentSizes.length; i++)
        vertexSegmentSizes[i] = bytesBuffer.getInt();

      float[] vertexData = new float[vertexCount * vertexSize];
      for(int i = 0; i < vertexData.length; i++)
        vertexData[i] = bytesBuffer.getFloat();

      int[] indexData = new int[indexCount];
      for(int i = 0; i < indexData.length; i++)
        indexData[i] = bytesBuffer.getInt();

      return Optional.of(new Mesh(vertexCount, indexCount, vertexSize, vertexSegmentCount, vertexSegmentSizes, vertexData, indexData));
    } catch (Exception e) {
      e.printStackTrace();
      Logger.error("Invalid mesh file cannot parse");
    }
    return Optional.empty();
  }

  private static void writeObjectAsMesh(Object value, OutputStream os) {
    if(value instanceof Mesh) {
      Mesh mesh = (Mesh)value;
      int byteCount = Mesh.calculateTotalSizeInBytes(mesh);
      ByteBuffer bytes = BufferUtils.createByteBuffer(byteCount);
      bytes.putInt(mesh.getVertexCount());
      bytes.putInt(mesh.getIndexCount());
      bytes.putInt(mesh.getVertexSize());
      bytes.putInt(mesh.getVertexSegmentCount());
      for(int v : mesh.getVertexSegmentSizes())
        bytes.putInt(v);
      for(float v : mesh.getVertexData())
        bytes.putFloat(v);
      for(int v : mesh.getIndexData())
        bytes.putInt(v);
      try {
        for (int i = 0; i < byteCount; i++)
          os.write(bytes.get(i));
      } catch (Exception e) {
        e.printStackTrace();
        Logger.error("Failed to write bytes");
      }
    }
  }

  private static void writeObjectAsShaderScript(Object value, OutputStream os) {
    StringBuilder source = new StringBuilder();
    ShaderScript shaderScript = (ShaderScript)value;
    for(String header : shaderScript.getSourceBlocks().keySet()) {
      source.append(String.format("@@%s\n", header));
      source.append(shaderScript.getSourceBlocks().get(header));
    }
    writeObjectAsString(source.toString(), os);
  }

  private static void writeObjectAsImage(Object value, OutputStream os) {}

  private static <T> void addObjectReader(ObjectReader reader, Class<T> tClass) {
    readers.put(tClass.getTypeName(), reader);
  }

  private static <T> void addObjectWriter(ObjectWriter writer, Class<T> tClass) {
    writers.put(tClass.getTypeName(), writer);
  }

}
