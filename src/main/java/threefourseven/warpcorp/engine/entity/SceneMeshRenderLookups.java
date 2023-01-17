package threefourseven.warpcorp.engine.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.asset.Material;
import threefourseven.warpcorp.engine.asset.Mesh;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class SceneMeshRenderLookups {
  private final Map<String, Map<String, Set<String>>> renderLookup;
  private final Map<String, Mesh> meshes;
  private final Map<String, Material> materials;
  private final Map<String, TransformComponent> transforms;
}
