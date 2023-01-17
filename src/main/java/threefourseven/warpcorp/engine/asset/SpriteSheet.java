package threefourseven.warpcorp.engine.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import threefourseven.warpcorp.engine.graphics.shader.shaderuniform.ShaderUniform;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.Optional;

@Getter
@Setter
public class SpriteSheet extends AutoIdentified {

  protected Asset imageAsset;
  protected Asset shaderScriptAsset;
  protected Asset meshAsset;
  protected ShaderUniform<Vector2f> spriteSheetSize = new ShaderUniform<>("spriteSheetSize", new Vector2f(1, 1));
  protected ShaderUniform<Integer> spriteSheetTexture = new ShaderUniform<>("spriteSheetTexture", 0);

  public SpriteSheet(Asset imageAsset, Asset shaderScriptAsset, Asset meshAsset) {
    this.imageAsset = imageAsset;
    this.shaderScriptAsset = shaderScriptAsset;
    this.meshAsset = meshAsset;
  }

  public SpriteSheet(Asset imageAsset, Asset shaderScriptAsset) {
    this(imageAsset, shaderScriptAsset, new Asset(AssetLocation.relative("./data/quad.wcm"), Mesh.class));
  }

  public SpriteSheet(Asset imageAsset) {
    this(imageAsset, new Asset(AssetLocation.relative("./data/sprite.wcs"), ShaderScript.class));
  }

  public SpriteSheet() {
    this(new Asset(AssetLocation.relative("./data/default_atlas.png"), Image.class));
  }

  @JsonIgnore
  public Optional<ShaderScript> getShaderScript() {
    if(shaderScriptAsset == null)
      return Optional.empty();
    return shaderScriptAsset.get();
  }

  @JsonIgnore
  public Optional<Image> getImage() {
    if(imageAsset == null)
      return Optional.empty();
    return imageAsset.get();
  }

  @JsonIgnore
  public Optional<Mesh> getMesh() {
    if(meshAsset == null)
      return Optional.empty();
    return meshAsset.get();
  }

}
