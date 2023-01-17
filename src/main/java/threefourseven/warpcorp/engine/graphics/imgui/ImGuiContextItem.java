package threefourseven.warpcorp.engine.graphics.imgui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.util.Named;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
@AllArgsConstructor
public class ImGuiContextItem implements Named {

  protected String name;
  protected Consumer<String> onSelect;
  protected Supplier<Boolean> shouldDisable;

  public ImGuiContextItem(String name, Consumer<String> onSelect) {
    this(name, onSelect, () -> false);
  }

}
