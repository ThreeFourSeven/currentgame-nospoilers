package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.entity.SceneUtil;

public class SaveSceneCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.trim().split(" ");

    if(args.length == 0) {
      result.missingRequired("scene_path");
      return result;
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("scene_path");
        continue;
      }
      SceneUtil.saveScene(arg);
      break;
    }

    return result;
  }

}
