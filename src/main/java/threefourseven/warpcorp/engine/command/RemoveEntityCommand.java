package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.entity.Scene;
import threefourseven.warpcorp.engine.entity.SceneUtil;

public class RemoveEntityCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.split(" ");
    Scene scene = SceneUtil.getScene();

    if(args.length < 1) {
      result.missingRequired("entity_id");
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("scene_path");
        continue;
      }
      scene.getEntity(arg).ifPresent(scene::removeEntity);
    }

    return result;
  }

}
