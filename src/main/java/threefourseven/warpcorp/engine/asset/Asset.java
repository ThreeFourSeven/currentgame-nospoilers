package threefourseven.warpcorp.engine.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import threefourseven.warpcorp.engine.io.IO;
import threefourseven.warpcorp.engine.logger.Logger;

import java.util.Optional;
import java.util.Set;

public class Asset {

  protected static final Set<String> readOnlyAssets = Set.of(
    Image.class.toString()
  );

  @Getter
  protected final AssetLocation location;

  @Getter
  protected final Class<?> dataClass;

  @Getter
  protected final boolean readOnly;

  public <T> Asset(AssetLocation location, T defaultValue, Class<?> dataClass, boolean readOnly) {
    this.location = location;
    this.dataClass = dataClass;
    this.readOnly = readOnly;
    if(!location.isNull()) {
      AssetUtil.getStore().store(this);
      if(defaultValue != null) {
        AssetUtil.getStore().setData(location.getPath(), defaultValue);
      }
    }
  }



  public <T> Asset(AssetLocation location, T defaultValue, Class<?> dataClass) {
    this(location, defaultValue, dataClass, readOnlyAssets.contains(dataClass.toString()));
  }

  public Asset(AssetLocation location, Class<?> dataClass) {
    this(location, null, dataClass);
  }

  public Asset() {
    this(new AssetLocation(), Object.class);
  }

  @JsonIgnore
  public Asset read() {
    Logger.debug(String.format("Reading %s asset from %s", location.getId(), location.getPath()));
    IO.readFileAs(location.getPath(), dataClass).ifPresent(data -> AssetUtil.getStore().setData(location.getPath(), data));
    if(!isDataNull()) {
      Logger.debug(String.format("Read %s asset from %s", location.getId(), location.getPath()));
    } else {
      Logger.error(String.format("Failed to read %s asset from %s", location.getId(), location.getPath()));
    }
    return this;
  }

  @JsonIgnore
  public Asset write() {
    if(readOnly)
      return this;
    if(!isDataNull()) {
      Logger.debug(String.format("Writing %s asset to %s", location.getId(), location.getPath()));
      AssetUtil.getStore().getData(location.getPath()).ifPresent(data -> IO.writeToFile(location.getPath(), data));
      Logger.debug(String.format("Wrote %s asset to %s", location.getId(), location.getPath()));
    } else {
      Logger.error(String.format("Failed to write %s asset to %s, data is null", location.getId(), location.getPath()));
    }
    return this;
  }

  public <T> Asset set(T value) {
    AssetUtil.getStore().setData(location.getPath(), value);
    return this;
  }

  @JsonIgnore
  public <T> Optional<T> get() {
    return AssetUtil.getStore().getData(location.getPath());
  }

  @JsonIgnore
  public boolean isDataNull() {
    return AssetUtil.getStore().getData(location.getPath()).isEmpty();
  }

}

