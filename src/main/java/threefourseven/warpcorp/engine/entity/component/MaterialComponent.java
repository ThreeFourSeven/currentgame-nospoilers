package threefourseven.warpcorp.engine.entity.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.asset.Asset;
import threefourseven.warpcorp.engine.asset.Material;

import java.util.Optional;

@Setter
@Getter
public class MaterialComponent implements Component {

  protected Asset materialAsset;

  public MaterialComponent(Asset materialAsset) {
    this.materialAsset = materialAsset;
  }

  public MaterialComponent() {
    this(null);
  }

  @Override
  public MaterialComponent copy() {
    return this;
  }

  @JsonIgnore
  public Optional<Material> getMaterial() {
    if(materialAsset == null)
      return Optional.empty();
    return materialAsset.get();
  }

}
