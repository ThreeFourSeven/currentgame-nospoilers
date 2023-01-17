package threefourseven.warpcorp.engine.command;

import threefourseven.warpcorp.engine.logger.Logger;
import threefourseven.warpcorp.engine.util.AutoIdentified;
import threefourseven.warpcorp.engine.util.Profiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Command extends AutoIdentified {

  private static final Map<String, Command> commands = new HashMap<>();

  public abstract CommandResult run(String arguments);

  protected CommandResult getResult() {
    return new CommandResult(this);
  }

  public static void initialize() {
    commands.put("create_entity", new CreateEntityCommand());
    commands.put("remove_entity", new RemoveEntityCommand());
    commands.put("remove_all_children", new RemoveAllChildrenCommand());
    commands.put("load_settings", new LoadSettingsCommand());
    commands.put("save_settings", new SaveSettingsCommand());
    commands.put("load_scene", new LoadSceneCommand());
    commands.put("save_scene", new SaveSceneCommand());
    commands.put("create_asset", new CreateAssetCommand());
    commands.put("remove_asset", new RemoveAssetCommand());
    commands.put("load_asset_store", new LoadAssetStoreCommand());
    commands.put("save_asset_store", new SaveAssetStoreCommand());
  }

  public static void runCommand(String command) {
    String[] segments = command.split(" ");
    String commandName = segments.length > 0 ? segments[0] : "";
    String arguments = segments.length > 1 ? String.join("", Arrays.copyOfRange(segments, 1, segments.length)) : "";
    if(commandName.length() > 0 && commands.containsKey(commandName)) {
      Logger.debug(String.format("Running command %s", command));
      Command cmd = commands.get(commandName);
      Profiler profiler = new Profiler();
      CommandResult result = profiler.profile(cmd::run, arguments);
      Logger.debug(String.format("Command %s took %.6fms to run", commandName, profiler.getLastMilliDelta()));
      if(result.isFailure()) {
        String fields = String.join(", ", result.getMissingArguments());
        Logger.warning(String.format("Missing fields %s", fields));
      }
    } else {
      Logger.warning(String.format("Unknown command %s", command));
    }
  }

}
