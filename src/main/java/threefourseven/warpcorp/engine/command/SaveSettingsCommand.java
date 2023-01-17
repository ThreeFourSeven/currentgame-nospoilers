package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.Settings;

public class SaveSettingsCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.split(" ");

    if(args.length < 1) {
      result.missingRequired("settings_path");
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("settings_path");
        continue;
      }
      Settings.save(arg);
      break;
    }

    return result;
  }

}
