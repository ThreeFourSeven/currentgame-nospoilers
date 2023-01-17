package threefourseven.warpcorp.engine.input;

import threefourseven.warpcorp.engine.io.IO;

public class Input {

  public static final Key key = new Key();
  public static Mouse mouse = new Mouse();

  public static InputCodes codes = new InputCodes();

  public static void initialize() {
    codes = IO.readJarFileAs("input_codes.json", InputCodes.class).orElse(new InputCodes());
    key.initialize();
    key.setCodes(codes.getKey());
    mouse.initialize();
    mouse.setCodes(codes.getMouse());
  }

  public static void update() {
    key.update();
    mouse.update();
  }

  public static void setCodes(InputCodes codes) {
    key.setCodes(codes.getKey());
    mouse.setCodes(codes.getMouse());
  }

}
