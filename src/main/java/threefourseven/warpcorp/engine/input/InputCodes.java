package threefourseven.warpcorp.engine.input;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
public class InputCodes {
  private final Map<String, Integer> mouse = new HashMap<>();
  private final Map<String, Integer> key = new HashMap<>();
}
