package threefourseven.warpcorp.engine.graphics;

import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import threefourseven.warpcorp.engine.Settings;
import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;
import threefourseven.warpcorp.engine.graphics.shader.ShaderProgram;
import threefourseven.warpcorp.engine.graphics.shader.shaderuniform.ShaderUniform;
import threefourseven.warpcorp.engine.graphics.texture.Texture;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiContext;
import threefourseven.warpcorp.engine.logger.Logger;
import threefourseven.warpcorp.engine.util.StreamUtil;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlApi implements GpuApi {

  private int transformBufferLimit;
  private int spriteBufferLimit;

  private ImGuiContext imGuiContext;

  private final Map<String, Integer> programIds = new HashMap<>();

  private final Map<String, Integer> vaos = new HashMap<>();
  private final Map<Integer, Integer> vbos = new HashMap<>();
  private final Map<Integer, Integer> tbos = new HashMap<>();
  private final Map<Integer, Integer> sdbos = new HashMap<>();
  private final Map<Integer, Integer> ibos = new HashMap<>();
  private final Map<Integer, FloatBuffer> instanceBuffers = new HashMap<>();
  private final Map<Integer, FloatBuffer> spriteDataBuffers = new HashMap<>();
  private final Map<Integer, Integer> instanceCounts = new HashMap<>();
  private final Map<Integer, Integer> spriteCounts = new HashMap<>();

  @Override
  public void initialize(Window window) {
    Logger.debug("Initializing GlApi");

    GL.createCapabilities();

    GL46.glEnable(GL46.GL_MULTISAMPLE);
    GL46.glEnable(GL46.GL_DEPTH);
    GL46.glEnable(GL46.GL_BLEND);
    GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);

    transformBufferLimit = Settings.getAs("transformBufferLimit", Integer.class).orElse(5000);
    spriteBufferLimit = Settings.getAs("spriteBufferLimit", Integer.class).orElse(5000);

    Logger.debug("Initialized GlApi");
  }

  @Override
  public void initializeImGui(Window window) {
    ImGui.createContext();
    imGuiContext = new ImGuiContext(window);
    imGuiContext.initialize();
  }

  @Override
  public void destroy() {
    for(Integer vao : vaos.values()) {
      GL46.glDeleteBuffers(vbos.get(vao));
      GL46.glDeleteBuffers(tbos.get(vao));
      GL46.glDeleteBuffers(ibos.get(vao));
      GL46.glDeleteVertexArrays(vao);
    }
    vaos.clear();
    vbos.clear();
    ibos.clear();
    tbos.clear();

    for(Integer programId : programIds.values()) {
      GL46.glDeleteProgram(programId);
    }
  }

  @Override
  public void clearFrame() {
    GL46.glClearColor(0, 0, 0, 1);
    GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
  }

  @Override
  public void viewport(int width, int height) {
    GL46.glViewport(0, 0, width, height);
  }

  @Override
  public Texture createTexture(Image image) {
    int id = GL46.glCreateTextures(GL46.GL_TEXTURE_2D);
    GL46.glBindTexture(GL46.GL_TEXTURE_2D, id);

    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);

    GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 1);

    int colorFormat = GL46.GL_RGBA;

    switch (image.getChannels()) {
      case 1:
        colorFormat = GL46.GL_R;
        break;
      case 2:
        colorFormat = GL46.GL_RG;
        break;
      case 3:
        colorFormat = GL46.GL_RGB;
        break;
    }

    GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, colorFormat, image.getWidth(), image.getHeight(), 0, colorFormat, GL46.GL_UNSIGNED_BYTE, image.getPixels());

    return new Texture(image, id);
  }

  @Override
  public void destroyTexture(Texture texture) {
    GL46.glDeleteTextures(texture.getId());
  }

  @Override
  public void useTexture(Texture texture) {
    GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture.getId());
    GL46.glBindTextureUnit(GL46.GL_TEXTURE0, texture.getId());
  }

  @Override
  public ShaderProgram createShaderProgram(ShaderScript shaderScript) {
    int programId = GL46.glCreateProgram();
    Set<Integer> shaders = new HashSet<>();
    for(String sectionType : shaderScript.getSourceBlocks().keySet()) {
      String blockSource = shaderScript.getSourceBlocks().get(sectionType);
      int shader = GL46.glCreateShader(getShaderTypeId(sectionType));
      GL46.glShaderSource(shader, blockSource);
      GL46.glCompileShader(shader);
      String log = GL46.glGetShaderInfoLog(shader);
      if(log.length() > 0) {
        Logger.warning(String.format("Encountered issue with shader source %s", blockSource));
        Logger.warning(log);
      } else {
        GL46.glAttachShader(programId, shader);
        shaders.add(shader);
      }
    }

    for(int shader : shaders) {
      GL46.glDeleteShader(shader);
    }

    GL46.glLinkProgram(programId);
    String log = GL46.glGetProgramInfoLog(programId);
    if(log.length() > 0) {
      Logger.warning("Encountered issue with shader program while linking");
      Logger.warning(log);
    }

    GL46.glValidateProgram(programId);
    log = GL46.glGetProgramInfoLog(programId);
    if(log.length() > 0) {
      Logger.warning("Encountered issue with shader program while validating");
      Logger.warning(log);
    }

    return new ShaderProgram(shaderScript, programId);
  }

  @Override
  public void useShaderProgram(ShaderProgram shaderProgram) {
    GL46.glUseProgram(shaderProgram.getId());
  }

  @Override
  public void destroyShaderProgram(ShaderProgram shaderProgram) {
    if(programIds.containsKey(shaderProgram.getShaderScript().getId())) {
      GL46.glDeleteProgram(programIds.get(shaderProgram.getShaderScript().getId()));
      programIds.remove(shaderProgram.getShaderScript().getId());
    }
  }

  @Override
  public void renderInstances(View view, Mesh mesh, Material material, Stream<TransformComponent> transforms) {
    material.getDiffuseImage().ifPresent(image -> {
      Texture texture = Graphics.createTexture(image);
      useTexture(texture);
      material.getDiffuseTexture().setValue(0);
    });
    material.getShaderScript().map(Graphics::createShaderProgram).ifPresent(shaderProgram -> {
      useShaderProgram(shaderProgram);
      loadShaderUniform(shaderProgram, material.getAmbient());
      loadShaderUniform(shaderProgram, material.getDiffuseTexture());
      loadShaderUniform(shaderProgram, view.getViewProjectionUniform());
      StreamUtil.chunk(transforms, transformBufferLimit)
        .forEach(chunkTransforms -> {
          for(TransformComponent transform : chunkTransforms)
            processMesh(mesh, transform);
          renderMeshes(mesh);
        });
    });
  }

  @Override
  public void renderSprites(SpriteRegistry spriteRegistry, View view, Stream<Sprite> spriteStream) {
    spriteStream.collect(Collectors.groupingBy(Sprite::getSpriteSheetName))
        .forEach((spriteSheetName, spriteList) -> spriteRegistry.getSpriteSheet(spriteSheetName)
          .ifPresent(spriteSheet -> {
            Stream<Sprite> sprites = spriteList.stream();
            spriteSheet.getImage().ifPresent(image -> {
              Texture texture = Graphics.createTexture(image);
              useTexture(texture);
              spriteSheet.getSpriteSheetTexture().setValue(0);
              spriteSheet.getSpriteSheetSize().setValue(new Vector2f(image.getWidth(), image.getHeight()));
            });
            spriteSheet.getShaderScript().map(Graphics::createShaderProgram).ifPresent(shaderProgram -> {
              useShaderProgram(shaderProgram);
              loadShaderUniform(shaderProgram, spriteSheet.getSpriteSheetSize());
              loadShaderUniform(shaderProgram, spriteSheet.getSpriteSheetTexture());
              loadShaderUniform(shaderProgram, view.getViewProjectionUniform());
              StreamUtil.chunk(sprites, spriteBufferLimit)
                .forEach(chunkSprites -> {
                  for(Sprite sprite : chunkSprites)
                    processSprite(spriteRegistry, spriteSheet, sprite);
                  renderSprites(spriteSheet);
                });
            });
          })
        );
  }

  @Override
  public <T> void loadShaderUniform(ShaderProgram shaderProgram, ShaderUniform<T> shaderUniform) {
    int location = GL46.glGetUniformLocation(shaderProgram.getId(), shaderUniform.getName());
    T value = shaderUniform.getValue();
    if(value instanceof Float) {
      GL46.glUniform1f(location, (float)value);
    } else if(value instanceof Integer) {
      GL46.glUniform1i(location, (int)value);
    } else if(value instanceof Vector2f) {
      Vector2f v = (Vector2f)value;
      GL46.glUniform2f(location, v.x, v.y);
    } else if(value instanceof Vector3f) {
      Vector3f v = (Vector3f)value;
      GL46.glUniform3f(location, v.x, v.y, v.z);
    } else if(value instanceof Vector4f) {
      Vector4f v = (Vector4f)value;
      GL46.glUniform4f(location, v.x, v.y, v.z, v.w);
    } else if(value instanceof Matrix4f) {
      Matrix4f m = (Matrix4f)value;
      GL46.glUniformMatrix4fv(location, false, m.get(new float[16]));
    }
  }

  @Override
  public ImGuiContext getImGuiContext() {
    return imGuiContext;
  }

  private void processMesh(Mesh mesh, TransformComponent transform) {
    createBufferObjects(mesh);
    loadMeshTransform(mesh, transform);
  }

  private void renderMeshes(Mesh mesh) {
    int vao = vaos.get(mesh.getId());
    int tbo = tbos.get(vao);
    int count = instanceCounts.get(vao);
    GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, tbo);
    GL46.glBufferSubData(GL46.GL_ARRAY_BUFFER, 0, instanceBuffers.get(vao));
    GL46.glBindVertexArray(vao);
    GL46.glDrawElementsInstanced(GL46.GL_TRIANGLES, mesh.getIndexCount(), GL46.GL_UNSIGNED_INT, 0, Math.min(count, transformBufferLimit));
    instanceCounts.put(vao, 0);
  }

  private void processSprite(SpriteRegistry spriteRegistry, SpriteSheet spriteSheet, Sprite sprite) {
    createBufferObjects(spriteSheet);
    loadSprite(spriteRegistry, spriteSheet, sprite);
  }

  private void renderSprites(SpriteSheet spriteSheet) {
    spriteSheet.getMesh().ifPresent(mesh -> {
      int vao = vaos.get(spriteSheet.getId());
      int sdbo = sdbos.get(vao);
      int count = spriteCounts.get(vao);
      GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, sdbo);
      GL46.glBufferSubData(GL46.GL_ARRAY_BUFFER, 0, spriteDataBuffers.get(vao));
      GL46.glBindVertexArray(vao);
      GL46.glDrawElementsInstanced(GL46.GL_TRIANGLES, mesh.getIndexCount(), GL46.GL_UNSIGNED_INT, 0, Math.min(count, spriteBufferLimit));
      spriteCounts.put(vao, 0);
    });
  }

  private int createVBO(Mesh mesh) {
    int bo = GL46.glCreateBuffers();
    GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, bo);
    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, mesh.getVertexData(), GL46.GL_STATIC_DRAW);
    int vertexSegmentIndex = 0;
    int vertexSegmentOffset = 0;
    int[] vertexSegmentSizes = mesh.getVertexSegmentSizes();
    while(vertexSegmentIndex < mesh.getVertexSegmentCount()) {
      int segmentSize = vertexSegmentSizes[vertexSegmentIndex];
      GL46.glEnableVertexAttribArray(vertexSegmentIndex);
      GL46.glVertexAttribPointer(vertexSegmentIndex, segmentSize, GL46.GL_FLOAT, false, Float.BYTES * mesh.getVertexSize(), (long)vertexSegmentOffset * Float.BYTES);
      vertexSegmentIndex++;
      vertexSegmentOffset += segmentSize;
    }
    return bo;
  }

  private int createTBO(Mesh mesh) {
    int bo = GL46.glCreateBuffers();
    GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, bo);
    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (long)16 * transformBufferLimit * Float.BYTES, GL46.GL_DYNAMIC_DRAW);
    for(int i = 0; i < 4; i++) {
      int index = i + mesh.getVertexSegmentCount();
      GL46.glEnableVertexAttribArray(index);
      GL46.glVertexAttribPointer(index, 4, GL46.GL_FLOAT, false, Float.BYTES * 16, (long)i * 4 * Float.BYTES);
      GL46.glVertexAttribDivisor(index, 1);
    }
    return bo;
  }

  private int createSDBO(Mesh mesh) {
    int bo = GL46.glCreateBuffers();
    GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, bo);
    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (long)9 * spriteBufferLimit * Float.BYTES, GL46.GL_DYNAMIC_DRAW);
    int index = mesh.getVertexSegmentCount();
    GL46.glEnableVertexAttribArray(index);
    GL46.glVertexAttribPointer(index, 3, GL46.GL_FLOAT, false, Float.BYTES * 9, 0);
    GL46.glVertexAttribDivisor(index, 1);
    index++;
    GL46.glEnableVertexAttribArray(index);
    GL46.glVertexAttribPointer(index, 2, GL46.GL_FLOAT, false, Float.BYTES * 9, 3 * Float.BYTES);
    GL46.glVertexAttribDivisor(index, 1);
    index++;
    GL46.glEnableVertexAttribArray(index);
    GL46.glVertexAttribPointer(index, 4, GL46.GL_FLOAT, false, Float.BYTES * 9, 5 * Float.BYTES);
    GL46.glVertexAttribDivisor(index, 1);
    return bo;
  }

  private int createIBO(Mesh mesh) {
    int bo = GL46.glCreateBuffers();
    GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, bo);
    GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexData(), GL46.GL_STATIC_DRAW);
    return bo;
  }

  private void createBufferObjects(Mesh mesh) {
    String id = mesh.getId();
    if(!vaos.containsKey(id)) {
      int vao = GL46.glCreateVertexArrays();
      vaos.put(id, vao);
      GL46.glBindVertexArray(vao);
      vbos.put(vao, createVBO(mesh));
      tbos.put(vao, createTBO(mesh));
      ibos.put(vao, createIBO(mesh));
      instanceCounts.put(vao, 0);
      instanceBuffers.put(vao, BufferUtils.createFloatBuffer(transformBufferLimit * 16));
    }
  }

  private void createBufferObjects(SpriteSheet spriteSheet) {
    String id = spriteSheet.getId();
    spriteSheet.getMesh().ifPresent(mesh -> {
      if(!vaos.containsKey(id)) {
        int vao = GL46.glCreateVertexArrays();
        vaos.put(id, vao);
        GL46.glBindVertexArray(vao);
        vbos.put(vao, createVBO(mesh));
        sdbos.put(vao, createSDBO(mesh));
        ibos.put(vao, createIBO(mesh));
        spriteCounts.put(vao, 0);
        spriteDataBuffers.put(vao, BufferUtils.createFloatBuffer(transformBufferLimit * 9));
      }
    });
  }

  private void loadMeshTransform(Mesh mesh, TransformComponent transform) {
    int vao = vaos.get(mesh.getId());
    int count = instanceCounts.get(vao);
    if(count < transformBufferLimit) {
      instanceBuffers.get(vao).put(count * 16, transform.getAbsoluteMatrix());
      instanceCounts.put(vao, count + 1);
    }
  }

  private void loadSprite(SpriteRegistry spriteRegistry, SpriteSheet spriteSheet, Sprite sprite) {
    int vao = vaos.get(spriteSheet.getId());
    int count = spriteCounts.get(vao);
    if(count < spriteBufferLimit) {
      FloatBuffer buffer = spriteDataBuffers.get(vao);
      int index = count * 9;
      spriteRegistry.getSpriteData(sprite.getName())
          .ifPresent(data -> {
            buffer.put(index, sprite.getX());
            buffer.put(index + 1, sprite.getY());
            buffer.put(index + 2, sprite.getZ());
            buffer.put(index + 3, sprite.getScaleX());
            buffer.put(index + 4, sprite.getScaleY());

            buffer.put(index + 5, data.getSheetOffsetX());
            buffer.put(index + 6, data.getSheetOffsetY());
            buffer.put(index + 7, data.getWidth());
            buffer.put(index + 8, data.getHeight());
            spriteCounts.put(vao, count + 1);
          });
    }
  }

  private int getShaderTypeId(String name) {
    switch (name.toLowerCase()) {
      case "fragment":
        return GL46.GL_FRAGMENT_SHADER;
      default:
      case "vertex":
        return GL46.GL_VERTEX_SHADER;
    }
  }

}
