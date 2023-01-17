package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.asset.Asset;
import threefourseven.warpcorp.engine.asset.AssetUtil;
import threefourseven.warpcorp.engine.logger.Logger;

import java.util.Optional;

public class RemoveAssetCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.split(" ");

    if(args.length < 1) {
      result.missingRequired("asset_path");
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("asset_path");
        continue;
      }
      Optional<Asset> asset = AssetUtil.getStore().get(arg);
      if(asset.isPresent()) {
        AssetUtil.getStore().remove(asset.get());
      } else {
        Logger.error(String.format("Failed to remove asset, could not find asset with path %s", arg));
      }
    }

    return result;
  }
}
