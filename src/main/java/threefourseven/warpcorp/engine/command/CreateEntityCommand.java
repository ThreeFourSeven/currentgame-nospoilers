package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.entity.SceneUtil;
import threefourseven.warpcorp.engine.entity.EntityUtil;

public class CreateEntityCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.split(" ");

    if(args.length < 1) {
      result.missingRequired("entity_generator");
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("scene_path");
        continue;
      }
      EntityUtil.generateTemplate(arg).ifPresent(SceneUtil::createEntityFromTemplate);
    }

    return result;
  }
}
