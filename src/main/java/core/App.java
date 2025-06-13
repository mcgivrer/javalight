package core;

import java.awt.event.KeyListener;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import core.gfx.Renderer;
import core.io.InputHandler;
import core.physic.PhysicSystem;
import core.scene.Scene;
import core.utils.Configuration;
import demo.DemoScene;

public class App {

    public enum RunningMode {
        DEV, TEST, PROD;
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    public static Configuration config;
    private static final long FPS = 60;

    public int debug = 0;
    public RunningMode mode = RunningMode.PROD;

    private boolean exit = false;
    public boolean pause = false;
    public long time = 0;

    private List<Scene> scenes = new ArrayList<>();
    private Scene currentScene;

    private Renderer renderer;
    private PhysicSystem physicSystem;
    private InputHandler inputHandler;

    public App() {
        log(App.class, LogLevel.INFO, "Welcome to %s (%s) !", messages.getString("app.name"),
                messages.getString("app.version"));
        initialize("/config.properties");
    }

    public void initialize(String configFilePath) {
        config = new Configuration(this).load(configFilePath).extractConfigValues();
        scenes.add(new DemoScene());
        currentScene = scenes.get(0);
    }

    public void run(String[] args) {

        config.parseArgs(args).extractConfigValues();

        inputHandler = new InputHandler(this);

        renderer = new Renderer(this);
        renderer.prepareWindow();

        physicSystem = new PhysicSystem(this);

        log(App.class, LogLevel.INFO, "RUN !");
        loop();
        dispose();
    }

    private void loop() {
        long startTime = 0, endTime = 0, elapsed = 0;
        currentScene.initialize(this);
        currentScene.create(this);
        endTime = System.currentTimeMillis();
        do {
            startTime = endTime;
            if (!pause) {
                update(elapsed);
            }
            render();
            try {
                Thread.sleep((1000 / (FPS)) - elapsed > 0 ? (1000 / (FPS)) - elapsed : 1);
            } catch (Exception e) {
                // something goes wrong in the matrix
            }
            endTime = System.currentTimeMillis();
            elapsed = endTime - startTime;

        } while (!exit && mode == RunningMode.PROD);
    }

    private void update(long elapsed) {

        currentScene.input(this);
        physicSystem.update(currentScene, elapsed);
    }

    private void render() {
        renderer.draw(currentScene);
    }

    private void dispose() {
        renderer.dispose();
    }

    public static void main(String[] args) {
        App app = new App();
        app.run(args);
    }

    public void setExit(boolean b) {
        exit = b;
    }

    public long getGameTime() {
        return time;
    }

    public static void log(Class<?> clazz, LogLevel ll, String message, Object... args) {
        String timestamp = ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String formattedMsg = args != null && args.length > 0 ? String.format(message, args) : message;
        String output = String.format("%s;%s;%s;%s", timestamp, ll, clazz.getName(), formattedMsg);
        if (ll == LogLevel.ERROR) {
            System.err.println(output);
        } else {
            System.out.println(output);
        }
    }

    public int getDebug() {
        return debug;
    }

    public void setDebug(int db) {
        debug = db;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pb) {
        pause = pb;
    }

    public Configuration getConfiguration() {
        return config;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public KeyListener getInputHandler() {
        return inputHandler;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }


}
