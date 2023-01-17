package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.asset.AssetUtil;

public class CreateAssetCommand extends Command {

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
      AssetUtil.create(arg);
    }

    return result;
  }
}
