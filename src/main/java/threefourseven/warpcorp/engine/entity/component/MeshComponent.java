package threefourseven.warpcorp.engine.entity.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.asset.Asset;
import threefourseven.warpcorp.engine.asset.Mesh;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.Optional;

@Setter
@Getter
public class MeshComponent extends AutoIdentified implements Component {

  protected Asset meshAsset = null;

  public MeshComponent(Asset meshAsset) {
    this.meshAsset = meshAsset;
  }

  public MeshComponent() {
  }

  @Override
  public MeshComponent copy() {
    return this;
  }

  @JsonIgnore
  public Optional<Mesh> getMesh() {
    if(meshAsset == null)
      return Optional.empty();
    return meshAsset.get();
  }

}
