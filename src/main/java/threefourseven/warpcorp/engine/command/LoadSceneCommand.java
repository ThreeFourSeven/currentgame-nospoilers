package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.entity.SceneUtil;

public class LoadSceneCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.split(" ");

    if(args.length < 1) {
      result.missingRequired("scene_path");
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("scene_path");
        continue;
      }
      SceneUtil.loadScene(arg);
      break;
    }

    return result;
  }

}
