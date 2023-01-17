package threefourseven.warpcorp.engine.graphics.imgui;

import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;
import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.entity.Entity;
import threefourseven.warpcorp.engine.entity.SceneUtil;
import threefourseven.warpcorp.engine.entity.component.*;
import threefourseven.warpcorp.engine.graphics.Graphics;
import threefourseven.warpcorp.engine.graphics.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImGuiUtil {

  private static final Map<String, ImGuiAssetInput> assetInputs = new HashMap<>();

  private static final Map<String, ImString> textInputs = new HashMap<>();

  private static String spriteSearchFilter = "";
  private static String spriteSheetSearchFilter = "";

  public static void textTable(List<String> data, int width, int height) {
    for(int i = 0; i < width; i++) {
      ImGui.separator();
      ImGui.columns(width);
      for(int j = 0; j < height; j++) {
        int index = i + j * width;
        if(index < data.size()) {
          ImGui.text(data.get(index));
          ImGui.nextColumn();
        } else {
          return;
        }
      }
      ImGui.separator();
    }
    ImGui.columns(1);
  }

  public static void floatTable(float[] data, int width, int height) {
    for(int i = 0; i < width; i++) {
      ImGui.separator();
      ImGui.columns(width);
      for(int j = 0; j < height; j++) {
        float mij = data[i + j * width];
        ImGui.text(String.format("%.2f", mij));
        ImGui.nextColumn();
      }
      ImGui.separator();
    }
    ImGui.columns(1);
  }

  public static void mesh(String title, Mesh mesh) {
    if(ImGui.collapsingHeader(title)) {
      ImGui.indent();
      ImGui.text(String.format("Vertex Count: %d", mesh.getVertexCount()));
      ImGui.text(String.format("Index Count: %d", mesh.getIndexCount()));
      ImGui.text(String.format("Vertex Size: %d", mesh.getVertexSize()));
      ImGui.text(String.format("Vertex Segment Count: %d", mesh.getVertexSegmentCount()));
      ImGui.unindent();
    }
  }

  public static void spriteData(String title, SpriteData spriteData) {
    if(ImGui.collapsingHeader(title)) {
      ImGui.indent();

      if(ImGui.collapsingHeader(String.format("SpriteSheet##%s", title))) {
        ImGui.indent();
        spriteSheetSearchFilter = textInput(title + " SpriteSheet Search", spriteSheetSearchFilter, 256);
        ImGui.text("Selected SpriteSheet");
        ImGui.sameLine();
        if(ImGui.beginCombo(String.format("##SelectedSpriteSheet%s", title), spriteData.getSpriteSheetName())) {
          Map<String, SpriteSheet> sprites = Graphics.getSpriteRegistry().getSpriteSheets();
          sprites.keySet()
            .stream().filter(name -> name.toLowerCase().contains(spriteSheetSearchFilter.toLowerCase()))
            .forEach(spriteSheetName -> {
              if(ImGui.selectable(spriteSheetName, spriteSheetName.equals(spriteData.getSpriteSheetName()))) {
                spriteData.setSpriteSheetName(spriteSheetName);
              }
            });
          ImGui.endCombo();
        }
        ImGui.unindent();
      }

      float[] ox = new float[]{ spriteData.getSheetOffsetX() };
      ImGui.text("Offset X");
      ImGui.sameLine();
      ImGui.dragFloat("##OffsetX", ox);
      spriteData.setSheetOffsetX(ox[0]);

      float[] oy = new float[]{ spriteData.getSheetOffsetY() };
      ImGui.text("Offset Y");
      ImGui.sameLine();
      ImGui.dragFloat("##Offset Y", oy);
      spriteData.setSheetOffsetY(oy[0]);

      float[] w = new float[]{ spriteData.getWidth() };
      ImGui.text("Width");
      ImGui.sameLine();
      ImGui.dragFloat("##Width", w);
      spriteData.setWidth(w[0]);

      float[] h = new float[]{ spriteData.getHeight() };
      ImGui.text("Height");
      ImGui.sameLine();
      ImGui.dragFloat("##Height", h);
      spriteData.setHeight(h[0]);

      ImGui.unindent();
    }
  }

  public static void sprite(String title, Sprite sprite) {
    if(ImGui.collapsingHeader(title)) {
      ImGui.indent();

      if(ImGui.collapsingHeader(String.format("Sprite##%s", title))) {
        ImGui.indent();
        spriteSearchFilter = textInput(title +" Sprite Search", spriteSearchFilter, 256);
        ImGui.text("Selected Sprite");
        ImGui.sameLine();
        if(ImGui.beginCombo(String.format("##SelectedSprite%s", title), sprite.getName())) {
          Map<String, SpriteData> sprites = Graphics.getSpriteRegistry().getSprites();
          sprites.keySet()
            .stream().filter(
              name -> sprites.get(name).getSpriteSheetName().equals(sprite.getSpriteSheetName()) &&
                name.toLowerCase().contains(spriteSearchFilter.toLowerCase())
            )
            .forEach(spriteName -> {
              if(ImGui.selectable(spriteName, spriteName.equals(sprite.getName()))) {
                sprite.setName(spriteName);
              }
            });
          ImGui.endCombo();
        }
        ImGui.unindent();
      }

      if(ImGui.collapsingHeader(String.format("SpriteSheet##%s", title))) {
        ImGui.indent();
        spriteSheetSearchFilter = textInput(title + " SpriteSheet Search", spriteSheetSearchFilter, 256);
        ImGui.text("Selected SpriteSheet");
        ImGui.sameLine();
        if(ImGui.beginCombo(String.format("##SelectedSpriteSheet%s", title), sprite.getSpriteSheetName())) {
          Map<String, SpriteSheet> sprites = Graphics.getSpriteRegistry().getSpriteSheets();
          sprites.keySet()
            .stream().filter(name -> name.toLowerCase().contains(spriteSheetSearchFilter.toLowerCase()))
            .forEach(spriteSheetName -> {
              if(ImGui.selectable(spriteSheetName, spriteSheetName.equals(sprite.getSpriteSheetName()))) {
                sprite.setSpriteSheetName(spriteSheetName);
              }
            });
          ImGui.endCombo();
        }
        ImGui.unindent();
      }

      float[] xyz = new float[]{ sprite.getX(), sprite.getY(), sprite.getZ() };
      ImGui.text("Position: ");
      ImGui.sameLine();
      ImGui.dragFloat3(String.format("##xyz%s", title), xyz);
      sprite.setX(xyz[0]);
      sprite.setY(xyz[1]);
      sprite.setZ(xyz[2]);

      float[] xy = new float[]{ sprite.getScaleX(), sprite.getScaleY() };
      ImGui.text("Scale: ");
      ImGui.sameLine();
      ImGui.dragFloat2(String.format("##xy%s", title), xy);
      sprite.setScaleX(xy[0]);
      sprite.setScaleY(xy[1]);

      ImGui.unindent();
    }
  }

  public static void image(String title, Image image) {
    if(ImGui.collapsingHeader(title)) {
      float width = ImGui.getContentRegionAvailX();
      ImGui.image(Graphics.createTexture(image).getId(), width, Math.min(width, ImGui.getContentRegionAvailY()));
    }
  }

  public static void shaderScript(String title, ShaderScript shaderScript) {
    if(ImGui.collapsingHeader(title)) {
      ImGui.indent();
      Map<String, String> sourceBlocks = shaderScript.getSourceBlocks();
      for(String sectionType : sourceBlocks.keySet()) {
        if(ImGui.collapsingHeader(sectionType)) {
          ImGui.textWrapped(sourceBlocks.get(sectionType));
        }
      }
      ImGui.unindent();
    }
  }

  public static void material(Window window, String title, Material material, boolean assetInputs) {
    if(ImGui.collapsingHeader(title)) {
      ImGui.indent();
      if(ImGui.collapsingHeader("Ambient Color")) {
        Vector4f ambient = material.getAmbient().getValue();
        float[] rgba = new float[]{ ambient.x, ambient.y, ambient.z, ambient.w };
        ImGui.colorPicker4("Ambient", rgba);
        material.getAmbient().setValue(new Vector4f(rgba[0], rgba[1], rgba[2], rgba[3]));
      }

      if(assetInputs) {
        ImGuiAssetInput imageAssetInput = getAssetInput(window, title + "Image", new ImGuiAssetInput(Image.class));
        imageAssetInput.setAsset(material.getDiffuseImageAsset());
        imageAssetInput.onRender(window);
        imageAssetInput.getAsset().ifPresent(material::setDiffuseImageAsset);
      }
      material.getDiffuseImage()
        .ifPresentOrElse(
          image -> image("Diffuse Image", image),
          () -> ImGui.text("No Image selected")
        );

      if(assetInputs) {
        ImGuiAssetInput shaderScriptAssetInput = getAssetInput(window, title + "ShaderScript", new ImGuiAssetInput(ShaderScript.class));
        shaderScriptAssetInput.setAsset(material.getShaderScriptAsset());
        shaderScriptAssetInput.onRender(window);
        shaderScriptAssetInput.getAsset().ifPresent(material::setShaderScriptAsset);
      }
      material.getShaderScript()
        .ifPresentOrElse(
          shaderScript -> shaderScript(title + "ShaderScript", shaderScript),
          () -> ImGui.text("No ScriptAsset selected")
        );
      ImGui.unindent();
    }
  }

  public static void material(Window window, String title, Material material) {
    material(window, title, material, false);
  }

  public static void spriteSheet(Window window, String title, SpriteSheet spriteSheet) {
    if(ImGui.collapsingHeader(title)) {
      Vector2f spriteSheetSize = spriteSheet.getSpriteSheetSize().getValue();
      ImGui.text(String.format("Sheet Width: %d", (int)spriteSheetSize.x));
      ImGui.text(String.format("Sheet Height: %d", (int)spriteSheetSize.y));

      ImGuiAssetInput imageAssetInput = getAssetInput(window, title + "SpriteSheetImage", new ImGuiAssetInput(Image.class));
      imageAssetInput.setAsset(spriteSheet.getImageAsset());
      imageAssetInput.onRender(window);
      imageAssetInput.getAsset().ifPresent(spriteSheet::setImageAsset);
      spriteSheet.getImage()
        .ifPresentOrElse(
          image -> image(title + "SpriteSheetImage", image),
          () -> ImGui.text("No Image Asset selected")
        );

      ImGuiAssetInput meshAssetInput = getAssetInput(window, "SpriteSheetMeshAsset", new ImGuiAssetInput(Mesh.class));
      meshAssetInput.setAsset(spriteSheet.getMeshAsset());
      meshAssetInput.onRender(window);
      meshAssetInput.getAsset().ifPresent(spriteSheet::setMeshAsset);
      spriteSheet.getMesh()
        .ifPresentOrElse(
          mesh -> mesh("SpriteSheetMeshAsset", mesh),
          () -> ImGui.text("No SpriteSheet MeshAsset selected")
        );
    }
  }

  public static void nameComponent(NameComponent name) {
    ImGui.beginGroup();
    name.setValue(textInput("Name:", name.getValue(), 256));
    ImGui.endGroup();
  }

  public static void transformComponent(Entity entity, TransformComponent transform) {
    if(ImGui.collapsingHeader("TransformComponent")) {
      ImGui.indent();
      ImGui.beginGroup();

      float[] position = new float[]{ transform.getPosition().x, transform.getPosition().y, transform.getPosition().z };
      ImGui.text("Position:");
      ImGui.sameLine();
      ImGui.dragFloat3("##position=input", position);
      transform.setPosition(position[0], position[1], position[2]);

      float[] scale = new float[]{  transform.getScale().x, transform.getScale().y, transform.getScale().z };
      ImGui.text("Scale:");
      ImGui.sameLine();
      ImGui.dragFloat3("##scale=input", scale);
      transform.setScale(scale[0], scale[1], scale[2]);

      float[] rotation = new float[]{  transform.getRotation().x, transform.getRotation().y, transform.getRotation().z };
      ImGui.text("Rotation:");
      ImGui.sameLine();
      ImGui.dragFloat3("##rotation=input", rotation);
      transform.setRotation(rotation[0], rotation[1], rotation[2]);

      SceneUtil.updateTransforms(entity);

      ImGui.endGroup();
      ImGui.unindent();
    }
  }

  public static void meshComponent(Window window, MeshComponent meshComponent) {
    if(ImGui.collapsingHeader("MeshComponent")) {
      ImGui.indent();
      ImGui.beginGroup();

      ImGuiAssetInput meshAssetInput = getAssetInput(window, "MeshAsset", new ImGuiAssetInput(Mesh.class));
      meshAssetInput.setAsset(meshComponent.getMeshAsset());
      meshAssetInput.onRender(window);
      meshAssetInput.getAsset().ifPresent(meshComponent::setMeshAsset);
      meshComponent.getMesh()
        .ifPresentOrElse(
          mesh -> mesh("MeshAsset", mesh),
          () -> ImGui.text("No MeshAsset selected")
        );

      ImGui.endGroup();
      ImGui.unindent();
    }
  }

  public static void materialComponent(Window window, MaterialComponent materialComponent) {
    if(ImGui.collapsingHeader("MaterialComponent")) {
      ImGui.indent();
      ImGui.beginGroup();

      ImGuiAssetInput materialAssetInput = getAssetInput(window, "MaterialAsset", new ImGuiAssetInput(Material.class));
      materialAssetInput.setAsset(materialComponent.getMaterialAsset());
      materialAssetInput.onRender(window);
      materialAssetInput.getAsset().ifPresent(materialComponent::setMaterialAsset);
      materialComponent.getMaterial()
        .ifPresentOrElse(
          material -> material(window, "Material", material, true),
          () -> ImGui.text("No MaterialAsset selected")
        );

      ImGui.endGroup();
      ImGui.unindent();
    }
  }

  public static void spriteLayerComponent(SpriteLayerComponent spriteLayerComponent) {
    if(ImGui.collapsingHeader("SpriteLayerComponent")) {
      ImGui.indent();
      ImGui.beginGroup();

      float[] xy = new float[]{ spriteLayerComponent.getX(), spriteLayerComponent.getY() };
      ImGui.text("Position: ");
      ImGui.sameLine();
      ImGui.dragFloat2("##xy", xy);
      spriteLayerComponent.setX(xy[0]);
      spriteLayerComponent.setY(xy[1]);

      if(ImGui.button("Add")) {
        spriteLayerComponent.getSprites().add(new Sprite("default_atlas", "default", 0, 0, 0, 1, 1));
      }

      int toRemove = -1;
      List<Sprite> toAdd = new ArrayList<>();

      int i = 0;
      for(Sprite sprite : spriteLayerComponent.getSprites()) {
        if(ImGui.collapsingHeader(String.format("Sprite Section %d", i))) {
          ImGui.indent();
          sprite(String.format("Sprite%d", i), sprite);
          if(ImGui.button(String.format("Duplicate %d", i))) {
            toAdd.add(new Sprite(sprite.getSpriteSheetName(), sprite.getName(), sprite.getX(), sprite.getY(), sprite.getZ(), sprite.getScaleX(), sprite.getScaleY()));
          }
          if(ImGui.button(String.format("Remove %d", i))) {
            toRemove = i;
          }
          ImGui.unindent();
        }
        i++;
      }

      if(toRemove > -1) {
        spriteLayerComponent.getSprites().remove(toRemove);
      }

      if(toAdd.size() > 0) {
        spriteLayerComponent.getSprites().addAll(toAdd);
      }

      ImGui.endGroup();
      ImGui.unindent();
    }
  }

  public static void animatedSpriteComponent(AnimatedSpriteComponent animatedSpriteComponent) {
    if(ImGui.collapsingHeader("AnimatedSpriteComponent")) {
      ImGui.indent();
      ImGui.beginGroup();

      float[] xy = new float[]{ animatedSpriteComponent.getX(), animatedSpriteComponent.getY() };
      ImGui.text("Position: ");
      ImGui.sameLine();
      ImGui.dragFloat2("##xy", xy);
      animatedSpriteComponent.setX(xy[0]);
      animatedSpriteComponent.setY(xy[1]);

      int[] frameTime = new int[]{ animatedSpriteComponent.getFrameTimeMs() };
      ImGui.text("FrameTime:");
      ImGui.sameLine();
      ImGui.dragInt("##FrameTime", frameTime);
      animatedSpriteComponent.setFrameTimeMs(frameTime[0]);

      if(ImGui.button("Add")) {
        animatedSpriteComponent.getSprites().add(new Sprite("default_atlas", "default", 0, 0, 0, 1, 1));
      }

      int toRemove = -1;
      List<Sprite> toAdd = new ArrayList<>();

      int i = 0;
      for(Sprite sprite : animatedSpriteComponent.getSprites()) {
        if(ImGui.collapsingHeader(String.format("Sprite Section %d", i))) {
          ImGui.indent();
          sprite(String.format("Sprite%d", i), sprite);
          if(ImGui.button(String.format("Duplicate %d", i))) {
            toAdd.add(new Sprite(sprite.getSpriteSheetName(), sprite.getName(), sprite.getX(), sprite.getY(), sprite.getZ(), sprite.getScaleX(), sprite.getScaleY()));
          }
          if(ImGui.button(String.format("Remove %d", i))) {
            toRemove = i;
          }
          ImGui.unindent();
        }
        i++;
      }

      if(toRemove > -1) {
        animatedSpriteComponent.getSprites().remove(toRemove);
      }

      if(toAdd.size() > 0) {
        animatedSpriteComponent.getSprites().addAll(toAdd);
      }

      ImGui.endGroup();
      ImGui.unindent();
    }
  }


  private static ImGuiAssetInput getAssetInput(Window window, String title, ImGuiAssetInput defaultValue) {
    if(!assetInputs.containsKey(title)) {
      defaultValue.onOpen(window);
      assetInputs.put(title, defaultValue);
    }
    return assetInputs.get(title);
  }

  public static String textInput(String title, String value, int limit) {
    if(!textInputs.containsKey(title)) {
      textInputs.put(title, new ImString(limit));
    }
    ImString imValue = textInputs.get(title);
    ImGui.text(title);
    ImGui.sameLine();
    imValue.set(value, false);
    ImGui.inputText("##"+title, imValue);
    return imValue.get();
  }

}
