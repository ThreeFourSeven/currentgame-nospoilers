package threefourseven.warpcorp.engine.entity.component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameComponent implements Component {

  protected String value;

  public NameComponent(String value) {
    this.value = value;
  }

  public NameComponent() {
    this("Default");
  }

  @Override
  public NameComponent copy() {
    return new NameComponent(value);
  }
}
