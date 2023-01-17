package threefourseven.warpcorp.engine.asset;

import lombok.Getter;
import threefourseven.warpcorp.engine.io.IO;
import threefourseven.warpcorp.engine.logger.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AssetUtil {
    
    @Getter
    protected static AssetStore store = new AssetStore();

    @Getter
    protected static Set<String> selectedAssets = new HashSet<>();

    public static Optional<Asset> create(AssetLocation location) {
        String path = location.getPath();
        if(location.exists()) {
            Asset asset;
            String extension = path.substring(path.lastIndexOf("."));
            switch(extension) {
                case ".wcmt":
                    asset = new Asset(location, Material.class);
                    break;
                case ".wcss":
                    asset = new Asset(location, SpriteSheet.class);
                    break;
                case ".wcs":
                    asset = new Asset(location, ShaderScript.class);
                    break;
                case ".wcm":
                    asset = new Asset(location, Mesh.class);
                    break;
                case ".png":
                case ".jpg":
                case ".jpeg":
                    asset = new Asset(location, Image.class);
                    break;
                default:
                    asset = new Asset(location, String.class);
                    break;
            }
            asset.read();
            return Optional.of(asset);
        } else {
            Logger.error(String.format("Failed to create asset, path %s does not exist", path));
        }
        return Optional.empty();
    }

    public static Optional<Asset> create(String path) {
        return create(AssetLocation.absolute(path));
    }

    public static void toggleSelection(Asset asset) {
        String path = asset.getLocation().getPath();
        if(selectedAssets.contains(path)) {
            selectedAssets.remove(path);
        } else {
            selectedAssets.clear();
            selectedAssets.add(path);
        }
    }

    public static void saveStore(String path) {
        store.getAssets().values().forEach(Asset::write);
        IO.writeToFile(path, store);
    }

    public static void loadStore(String path) {
        store.set(IO.readFileAs(path, AssetStore.class).orElse(store));
        for(Asset asset : store.getAssets().values()) {
            asset.read();
            if(asset.isDataNull()) {
                Logger.warning(String.format("Read empty asset value from %s", asset.getLocation().getPath()));
            }
        }
    }

}
