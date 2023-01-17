package threefourseven.warpcorp.engine.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ShaderScript extends AutoIdentified {

  private final Map<String, String> sourceBlocks;

}
