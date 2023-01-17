package threefourseven.warpcorp.engine.util;

import java.util.UUID;

public class AutoIdentified implements Identified,  Comparable<AutoIdentified> {

  protected String id;

  protected AutoIdentified(String id) {
    this.id = id;
  }

  protected AutoIdentified() {
    this(UUID.randomUUID().toString());
  }

  @Override
  public String getId() {
    return id;
  }
  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }
    if (!(obj instanceof AutoIdentified)) {
      return false;
    }

    AutoIdentified that = (AutoIdentified)obj;
    return id.equals(that.id);
  }

  @Override
  public int compareTo(AutoIdentified o) {
    return id.compareTo(o.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
