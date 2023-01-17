package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.Settings;

public class LoadSettingsCommand extends Command {

  @Override
  public CommandResult run(String arguments) {
    CommandResult result = getResult();
    String[] args = arguments.split(" ");

    if(args.length < 1 || args[0].isEmpty()) {
      result.missingRequired("settings_path");
    }

    for(String arg : args) {
      if(arg.isEmpty()) {
        result.missingRequired("settings_path");
        continue;
      }
      Settings.load(arg);
      break;
    }

    return result;
  }

}
