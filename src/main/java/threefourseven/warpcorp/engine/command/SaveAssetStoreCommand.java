package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.asset.AssetUtil;

public class SaveAssetStoreCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.trim().split(" ");

    if(args.length == 0) {
      result.missingRequired("asset_store_path");
      return result;
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("asset_store_path");
        continue;
      }
      AssetUtil.saveStore(arg);
      break;
    }

    return result;
  }

}
