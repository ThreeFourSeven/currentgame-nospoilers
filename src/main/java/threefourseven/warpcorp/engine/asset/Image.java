package threefourseven.warpcorp.engine.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class Image extends AutoIdentified {

  protected final int width;

  protected final int height;

  protected final int channels;

  @JsonIgnore
  protected final ByteBuffer pixels;

}
