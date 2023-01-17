package threefourseven.warpcorp.engine.graphics.imgui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import threefourseven.warpcorp.engine.util.Named;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
@AllArgsConstructor
public class ImGuiContextItemMenu implements Named {

  protected String name;
  protected List<Named> options;

  public ImGuiContextItemMenu(String name, Collection<Named> options) {
    this.name = name;
    this.options = new ArrayList<>();
    for(Named option : options) {
      if(option instanceof ImGuiContextItem || option instanceof ImGuiContextItemMenu) {
        this.options.add(option);
      }
    }
  }

}
