package threefourseven.warpcorp.engine.graphics.editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import threefourseven.warpcorp.WarpCorp;
import threefourseven.warpcorp.engine.graphics.Window;
import threefourseven.warpcorp.engine.graphics.imgui.ImGuiWindow;
import threefourseven.warpcorp.engine.logger.LogLevel;
import threefourseven.warpcorp.engine.logger.LogMessage;
import threefourseven.warpcorp.engine.logger.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditorLogger extends ImGuiWindow {

  private final Map<LogLevel, ImBoolean> showLevelMap = Map.of(
    LogLevel.Info, new ImBoolean(true),
    LogLevel.Debug, new ImBoolean(true),
    LogLevel.Warning, new ImBoolean(true),
    LogLevel.Error, new ImBoolean(true),
    LogLevel.Critical, new ImBoolean(true)
  );

  public EditorLogger() {
    super("EditorLogger", WarpCorp.isDevMode());
  }

  @Override
  public void onOpen(Window window, Object... properties) {

  }

  @Override
  public void onRender(Window window) {
    ImGui.text(String.format("EditorLogger | average frame time = %.3f ms | average fps = %dfps", WarpCorp.getFrameTimeMs(), (int)WarpCorp.getFramesPerSecond()));

    Set<LogLevel> levels = showLevelMap.keySet();
    int count = 0;
    for(LogLevel level : levels) {
      ImBoolean show = showLevelMap.get(level);
      ImGui.checkbox(level.toString(), show);
      count++;
      if(count < levels.size())
        ImGui.sameLine();
    }

    if(ImGui.beginChild("EditorTerminal", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar)) {
      List<LogMessage> messages = Logger.getMessages();
      for(LogMessage message : messages) {
        if(showLevelMap.get(message.getLevel()).get()) {
          ImGui.textColored(getLogColor(message.getLevel()), message.getMessage());
        }
      }
      ImGui.endChild();
    }
  }

  @Override
  public void onClose(Window window) {

  }

  private int getLogColor(LogLevel level) {
    switch(level) {
      case Info: return 0xff00ff00;
      case Debug: return 0xff99086b;
      case Warning: return 0xff00ffff;
      case Error: return 0xff0000ff;
      case Critical: return 0xff107fe0;
    }
    return 0xffffffff;
  }
}
