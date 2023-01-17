package threefourseven.warpcorp;

import lombok.Getter;
import threefourseven.warpcorp.engine.Settings;
import threefourseven.warpcorp.engine.command.*;
import threefourseven.warpcorp.engine.entity.component.AnimatedSpriteComponent;
import threefourseven.warpcorp.engine.event.*;
import threefourseven.warpcorp.engine.graphics.Graphics;
import threefourseven.warpcorp.engine.input.Input;
import threefourseven.warpcorp.engine.io.IO;
import threefourseven.warpcorp.engine.logger.Logger;
import threefourseven.warpcorp.engine.entity.EntityUtil;
import threefourseven.warpcorp.engine.util.Profiler;
import threefourseven.warpcorp.engine.util.SystemUtil;

public class WarpCorp {

  @Getter
  private static boolean shouldClose = false;

  @Getter
  private static float goalFrameTime = 16f;

  @Getter
  private static float frameTimeMs = goalFrameTime;

  @Getter
  private static float framesPerSecond = 1f / (frameTimeMs / 1000f);

  @Getter
  private static final boolean devMode = SystemUtil.getEnvironmentVariableAs("WARPCORP_DEVMODE", Boolean::parseBoolean).orElse(false);

  public static void run() {
    Profiler initializeProfiler = new Profiler();
    initializeProfiler.profile(WarpCorp::initialize);
    Logger.debug(String.format("WarpCorp initialized in %.6fs", initializeProfiler.getLastMilliDelta() / 1000f));

    goalFrameTime = Settings.getAs("goalFrameTime", Double.class).orElse((double)goalFrameTime).floatValue();
    while(!shouldClose) {
      frameTimeMs = Profiler.mainLoopProfiler.profile(WarpCorp::loop) / 1_000_000f;
      framesPerSecond = 1 / (frameTimeMs / 1000f);
    }

    float averageFrameTimeMs = Profiler.calculateAverageFrameTimeMs();
    float averageFramesPerSecond = Profiler.calculateAverageFramesPerSecond();
    Logger.debug(String.format("WarpCorp loop average complete time %.6fms", averageFrameTimeMs));
    Logger.debug(String.format("WarpCorp loop average frames per second (fps) %.2f", averageFramesPerSecond));

    Profiler destroyProfiler = new Profiler();
    destroyProfiler.profile(WarpCorp::destroy);
    Logger.debug(String.format("WarpCorp destroyed in %.6fs", destroyProfiler.getLastMilliDelta() / 1000f));
    IO.writeToFile("logs.txt", String.join("\n", Logger.collectMessages()));
  }

  public static void stop() {
    shouldClose = true;
  }

  private static void initialize() {
    Logger.debug("Initializing Warp-Corp");
    Command.initialize();
    IO.initialize();

    Command.runCommand("load_settings settings.json");
    Command.runCommand("load_asset_store assets.json");

    EntityUtil.initialize();
    Graphics.initialize();
    Input.initialize();

    Command.runCommand("load_scene scene.json");

    Logger.debug("Initialized Warp-Corp");
  }

  private static void loop() {
    EventState.poll();
    Graphics.render();
  }

  private static void destroy() {
    Logger.debug("Destroying Warp-Corp");

    Command.runCommand("save_asset_store assets.json");
    Command.runCommand("save_scene scene.json");
    Command.runCommand("save_settings settings.json");
    Graphics.destroy();

    Logger.debug("Destroyed Warp-Corp");
  }

  public static float getFrameDelta() {
    return Math.max(goalFrameTime, frameTimeMs);
  }

}
