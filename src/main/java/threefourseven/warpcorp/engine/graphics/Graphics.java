package threefourseven.warpcorp.engine.graphics;

import imgui.ImGui;
import lombok.Getter;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import threefourseven.warpcorp.engine.Settings;
import threefourseven.warpcorp.WarpCorp;
import threefourseven.warpcorp.engine.asset.*;
import threefourseven.warpcorp.engine.entity.SceneMeshRenderLookups;
import threefourseven.warpcorp.engine.entity.SceneUtil;
import threefourseven.warpcorp.engine.entity.component.AnimatedSpriteComponent;
import threefourseven.warpcorp.engine.entity.component.TransformComponent;
import threefourseven.warpcorp.engine.graphics.editor.EditorAssetStore;
import threefourseven.warpcorp.engine.graphics.editor.EditorLogger;
import threefourseven.warpcorp.engine.graphics.editor.EditorScene;
import threefourseven.warpcorp.engine.event.EventState;
import threefourseven.warpcorp.engine.event.WindowResizeEvent;
import threefourseven.warpcorp.engine.event.listener.filter.KeyFilter;
import threefourseven.warpcorp.engine.event.listener.filter.WindowCloseFilter;
import threefourseven.warpcorp.engine.event.listener.filter.WindowResizeFilter;
import threefourseven.warpcorp.engine.event.producer.*;
import threefourseven.warpcorp.engine.graphics.editor.EditorSpriteRegistry;
import threefourseven.warpcorp.engine.graphics.shader.ShaderProgram;
import threefourseven.warpcorp.engine.graphics.texture.Texture;
import threefourseven.warpcorp.engine.input.Input;
import threefourseven.warpcorp.engine.io.IO;
import threefourseven.warpcorp.engine.logger.Logger;
import threefourseven.warpcorp.engine.util.Profiler;

import java.util.*;
import java.util.stream.Stream;

public class Graphics {

  public static final Asset quadAsset = new Asset(AssetLocation.relative("./data/quad.wcm"), Mesh.class);
  public static final Asset triangleAsset = new Asset(AssetLocation.relative("./data/triangle.wcm"), Mesh.class);
  public static final Asset defaultMaterial = new Asset(AssetLocation.relative("./data/default.wcmt"), Material.class);

  public static final Asset defaultSpriteSheet = new Asset(AssetLocation.relative("./data/default_atlas.wcss"), SpriteSheet.class);

  @Getter
  private static final GpuApi gpu = new GlApi();

  @Getter
  private static SpriteRegistry spriteRegistry = new SpriteRegistry();

  @Getter
  protected static final Map<String, Texture> textures = new HashMap<>();

  @Getter
  protected static final Map<String, ShaderProgram> shaderPrograms = new HashMap<>();

  @Getter
  protected static final View view = new View(new Vector3f(0, 0, 5));

  @Getter
  private static Window window;

  public static void initialize() {
    Logger.debug("Initializing Graphics");
    GLFW.glfwInit();

    createWindow();
    Graphics.createWindowContext(window);

    EventState.addEventProducer(new WindowResizeEventProducer(window));
    EventState.addEventProducer(new WindowCloseEventProducer(window));
    EventState.addEventProducer(new KeyEventProducer(window));
    EventState.addEventProducer(new MouseButtonEventProducer(window));
    EventState.addEventProducer(new MouseMotionEventProducer(window));
    EventState.addEventProducer(new MouseScrollEventProducer(window));

    EventState.addEventListener(new WindowResizeFilter().toListener(event -> {
      WindowResizeEvent e = (WindowResizeEvent)event;
      window.setWidth(e.getWidth());
      window.setHeight(e.getHeight());
      gpu.viewport(e.getWidth(), e.getHeight());
    }));

    EventState.addEventListener(new WindowCloseFilter().toListener(WarpCorp::stop));

    if(WarpCorp.isDevMode()) {
      EventState.addEventListener(new KeyFilter(Set.of("escape"), GLFW.GLFW_RELEASE).toListener(WarpCorp::stop));
    }

    if(WarpCorp.isDevMode()) {
      initializeImGui();
    }

    IO.readFileAs("spriteRegistry.json", SpriteRegistry.class)
        .ifPresent(spriteRegistry -> Graphics.spriteRegistry = spriteRegistry);

    Logger.debug("Initialized Graphics");
  }

  public static void renderInstances(Mesh mesh, Material material, Collection<TransformComponent> transforms) {
    renderInstances(mesh, material, transforms.stream());
  }

  public static void renderInstances(Mesh mesh, Material material, Stream<TransformComponent> transformStream) {
    gpu.renderInstances(view, mesh, material, transformStream);

  }

  public static void renderSprites(Collection<Sprite> sprites) {
    renderSprites(sprites.stream());
  }


  public static void renderSprites(Stream<Sprite> sprites) {
    gpu.renderSprites(spriteRegistry, view, sprites);
  }

  public static void render() {
    AnimatedSpriteComponent.updateFrameTimeCounters();
    clearFrame();

    view.update(window);

    renderSprites(SceneUtil.getSceneSprites());

    SceneMeshRenderLookups meshLookups = SceneUtil.getSceneMeshRenderLookups();
    Map<String, Map<String, Set<String>>> meshRenderLookup = meshLookups.getRenderLookup();
    Map<String, Mesh> meshes = meshLookups.getMeshes();
    Map<String, Material> materials = meshLookups.getMaterials();
    Map<String, TransformComponent> transforms = meshLookups.getTransforms();
    meshRenderLookup.keySet().stream().map(materials::get).forEach(material -> {
      Map<String, Set<String>> subRenderLookup = meshRenderLookup.get(material.getId());
      subRenderLookup.keySet().stream().map(meshes::get)
        .forEach(mesh -> renderInstances(mesh, material, subRenderLookup.get(mesh.getId()).parallelStream().map(transforms::get)));
    });

    swapFrame();

    float dz = -Input.mouse.getScrollY() * WarpCorp.getFrameDelta() / 100f;
    if(Math.abs(dz) > 0 && view.position.z + dz >= 1) {
      view.position.z += dz;
    }
    if(Input.mouse.isDown("right")) {
      if(Math.abs(Input.mouse.getDx()) > 0) {
        view.position.x += -10 * Input.mouse.getDx() * Math.abs(view.position.z) / (float)window.getWidth();
      }
      if(Math.abs(Input.mouse.getDy()) > 0) {
        view.position.y += 10 * Input.mouse.getDy() * Math.abs(view.position.z) / (float)window.getHeight();
      }
    }
    AnimatedSpriteComponent.resetFrameTimeCounters();
  }

  public static void destroy() {
    IO.writeToFile("spriteRegistry.json", spriteRegistry);
    window.destroy();
    GLFW.glfwTerminate();
  }

  public static Texture createTexture(Image image) {
    if(textures.containsKey(image.getId())) {
      return textures.get(image.getId());
    }

    Profiler profiler = new Profiler();
    Texture texture = profiler.profile(gpu::createTexture, image);
    Logger.debug(String.format("Created texture %s in %.6fms", image.getId(), profiler.getLastMilliDelta()));
    textures.put(image.getId(), texture);
    return texture;
  }

  public static void destroyTexture(Image image) {
    if(textures.containsKey(image.getId())) {
      Profiler profiler = new Profiler();
      profiler.profile(gpu::destroyTexture, textures.get(image.getId()));
      Logger.debug(String.format("Destroyed texture %s in %.6fms", image.getId(), profiler.getLastMilliDelta()));
      textures.remove(image.getId());
    }
  }

  public static ShaderProgram createShaderProgram(ShaderScript shaderScript) {
    if(shaderPrograms.containsKey(shaderScript.getId())) {
      return shaderPrograms.get(shaderScript.getId());
    }

    Profiler profiler = new Profiler();
    ShaderProgram shaderProgram = profiler.profile(gpu::createShaderProgram, shaderScript);
    Logger.debug(String.format("Created shader program %s in %.6fms", shaderProgram.getId(), profiler.getLastMilliDelta()));
    shaderPrograms.put(shaderScript.getId(), shaderProgram);

    return shaderProgram;
  }

  public static void destroyShaderProgram(ShaderScript shaderScript) {
    if(shaderPrograms.containsKey(shaderScript.getId())) {
      Profiler profiler = new Profiler();
      profiler.profile(gpu::destroyShaderProgram, shaderPrograms.get(shaderScript.getId()));
      Logger.debug(String.format("Destroyed shader program %s in %.6fms", shaderScript.getId(), profiler.getLastMilliDelta()));
      shaderPrograms.remove(shaderScript.getId());
    }
  }

  private static void initializeImGui() {
    Logger.debug("Initializing ImGui");
    gpu.initializeImGui(window);
    gpu.getImGuiContext().addComponent(new EditorScene());
    gpu.getImGuiContext().addComponent(new EditorLogger());
    gpu.getImGuiContext().addComponent(new EditorAssetStore());
    gpu.getImGuiContext().addComponent(new EditorSpriteRegistry());
    Logger.debug("Initialized ImGui");
  }

  private static void clearFrame() {
    Graphics.gpu.clearFrame();
  }

  private static void swapFrame() {
    if(gpu.getImGuiContext() != null) {
      gpu.getImGuiContext().swapFrame();
    }
    window.swapFrame();
  }

  private static void createWindow() {
    Logger.debug("Creating Window");

    int width = Settings.getAs("windowWidth", Integer.class).orElse(1920);
    int height = Settings.getAs("windowHeight", Integer.class).orElse(1080);
    String title = Settings.getAs("windowTitle", String.class).orElse("Window");

    GLFW.glfwDefaultWindowHints();

    GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, Settings.getAs("borderless", Boolean.class).orElse(false) ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);

    if(Settings.getAs("msaa", Boolean.class).orElse(false)) {
      GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, Settings.getAs("msaaSamples", Integer.class).orElse(4));
    }

    long glfwWindowId = GLFW.glfwCreateWindow(width, height, title, 0, 0);

    window = new Window(title, width, height, glfwWindowId);

    Logger.debug("Created Window");
  }

  private static void createWindowContext(Window window) {
    Logger.debug("Creating Window Context");

    GLFW.glfwMakeContextCurrent(window.getGlfwId());
    gpu.initialize(window);

    GLFW.glfwSwapInterval(Settings.getAs("vsync", Boolean.class).orElse(false) ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);

    Logger.debug("Created Window Context");
  }

}
