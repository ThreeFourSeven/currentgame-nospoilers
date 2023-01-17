package threefourseven.warpcorp.engine.asset;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import threefourseven.warpcorp.engine.graphics.Graphics;

public class AssetStore {

    @Getter
    protected final Map<String, Asset> assets = new ConcurrentHashMap<>();

    @JsonIgnore
    protected final Map<String, AssetDataWrapper<?>> assetData = new ConcurrentHashMap<>();

    public void remove(Asset asset) {
        String path = asset.getLocation().getPath();
        get(path).flatMap(Asset::get).ifPresent(data -> {
            if(data instanceof Image) {
                Graphics.destroyTexture((Image)data);
            }
        });
        assets.remove(path);
        assetData.remove(path);
    }

    public void set(AssetStore store) {
        assets.clear();
        assetData.clear();
        assets.putAll(store.getAssets());
    }

    public Optional<Asset> get(String path) {
        if(assets.containsKey(path)) {
           return Optional.of(assets.get(path));
        }
        return Optional.empty();
    }

    protected Asset store(Asset asset) {
        assets.put(asset.getLocation().getPath(), asset);
        return asset;
    }

    protected <T> Optional<T> getData(String path) {
        if(assetData.containsKey(path)) {
            return Optional.of((T)assetData.get(path).getData());
        }
        return Optional.empty();
    }

    protected <T> void setData(String path, T data) {
        assetData.put(path, new AssetDataWrapper<>(data));
    }

}
