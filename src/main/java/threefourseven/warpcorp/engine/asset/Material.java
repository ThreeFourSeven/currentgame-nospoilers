package threefourseven.warpcorp.engine.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;
import threefourseven.warpcorp.engine.graphics.shader.shaderuniform.ShaderUniform;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.Optional;

@Getter
@Setter
public class Material extends AutoIdentified {

  protected Asset shaderScriptAsset;

  protected Asset diffuseImageAsset;

  protected ShaderUniform<Vector4f> ambient = new ShaderUniform<>("ambient", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
  protected ShaderUniform<Integer> diffuseTexture = new ShaderUniform<>("diffuseTexture", 0);

  public Material(Asset shaderScriptAsset, Asset diffuseImageAsset) {
    this.shaderScriptAsset = shaderScriptAsset;
    this.diffuseImageAsset = diffuseImageAsset;
  }

  public Material(Asset diffuseImageAsset) {
    this(new Asset(AssetLocation.relative("./data/default.wcs"), ShaderScript.class), diffuseImageAsset);
  }

  public Material() {
    this(new Asset(AssetLocation.relative("./data/default.png"), Image.class));
  }

  @JsonIgnore
  public Optional<ShaderScript> getShaderScript() {
    if(shaderScriptAsset == null)
      return Optional.empty();
    return shaderScriptAsset.get();
  }

  @JsonIgnore
  public Optional<Image> getDiffuseImage() {
    if(diffuseImageAsset == null)
      return Optional.empty();
    return diffuseImageAsset.get();
  }

}
