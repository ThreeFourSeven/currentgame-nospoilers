package threefourseven.warpcorp.engine.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import threefourseven.warpcorp.engine.util.AutoIdentified;

import java.io.File;
import java.nio.file.FileSystems;

@Getter
public class AssetLocation extends AutoIdentified {

  protected String path;

  public AssetLocation(String path) {
    super(path);
    this.path = path;
  }

  public AssetLocation() {
    this("");
  }

  @JsonIgnore
  public boolean isNull() {
    return path.isEmpty();
  }

  @JsonIgnore
  public boolean exists() {
    return !isNull() && new File(path).exists();
  }

  public static AssetLocation absolute(String path) {
    return new AssetLocation(path);
  }

  public static AssetLocation relative(String path) {
    return absolute(FileSystems.getDefault().getPath(path).normalize().toAbsolutePath().toString());
  }

}
