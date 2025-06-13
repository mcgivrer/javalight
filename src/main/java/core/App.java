package core;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import core.entity.Entity;
import core.entity.World;
import core.gfx.Renderer;
import core.physic.PhysicType;
import core.scene.Scene;
import demo.DemoScene;

public class App implements KeyListener {

    public enum RunningMode {
        DEV, TEST, PROD;
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    public static Properties config = new Properties();
    private static final long FPS = 60;
    private boolean[] keys = new boolean[1024];

    public int debug = 0;
    public RunningMode mode = RunningMode.PROD;

    private boolean exit = false;
    public boolean pause = false;
    private long time = 0;

    private List<Scene> scenes = new ArrayList<>();
    private Scene currentScene;

    private Renderer renderer;

    public App() {
        log(App.class, LogLevel.INFO, "Welcome to %s (%s) !", messages.getString("app.name"),
                messages.getString("app.version"));
    }

    public void initConfig(String configFilePath) {
        try {
            config.load(this.getClass().getResourceAsStream(configFilePath));
        } catch (Exception e) {
            log(App.class, LogLevel.ERROR, "Unable to read configuration file %s: %s", configFilePath, e.getMessage());
        }
        scenes.add(new DemoScene());
        currentScene = scenes.get(0);
    }

    public void run(String[] args) {
        initConfig("/config.properties");
        parseArgs(args);
        extractConfigValues();

        renderer = new Renderer(this);
        renderer.prepareWindow();

        log(App.class, LogLevel.INFO, "RUN !");
        loop();
        dispose();
    }

    public void parseArgs(String[] args) {
        log(App.class, LogLevel.INFO, "Parse command line arguments...");
        for (String arg : args) {
            String[] keyVal = arg.split("=");
            config.setProperty(keyVal[0], keyVal[1]);
            log(App.class, LogLevel.INFO, " |_ Override config:%s=%s", keyVal[0], keyVal[1]);
        }
    }

    private void extractConfigValues() {
        log(App.class, LogLevel.INFO, "Read configuration...");
        for (String key : config.stringPropertyNames()) {
            switch (key) {
            case "app.debug", "debug", "d" -> {
                debug = Integer.parseInt(config.getProperty(key, "0"));
                log(App.class, LogLevel.INFO, "=> debug level set to %d", debug);
            }
            case "app.mode", "mode", "m" -> {
                mode = RunningMode.valueOf(config.getProperty(key, "PROD"));
            }
            default -> {
            }
            }
        }
    }

    public <T> T getConfig(String key, T defaultValue) {
        switch (key) {
        case "app.debug", "debug", "d" -> {
            return (T) Integer.valueOf(config.getProperty(key, "0"));
        }
        case "app.mode", "mode", "m" -> {
            return (T) RunningMode.valueOf(config.getProperty(key, "PROD"));
        }
        case "app.window.size", "ws" -> {
            String[] size = config.getProperty(key, "720x460").split("x");
            return (T) new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
        }
        case "app.gfx.rendering.buffer.size", "rbs" -> {
            String[] size = config.getProperty(key, "320x200").split("x");
            return (T) new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
        }
        default -> log(App.class, LogLevel.WARN, "Unknown configuration key %s", key);
        }
        return null;
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

        time += elapsed;

        currentScene.input(this);
        currentScene.getEntities().stream().filter(Entity::isActive).forEach(e -> {
            updateEntity(e, elapsed);
            e.update(elapsed);
            constrainsEntity(currentScene.getWorld(), e);
        });
        currentScene.getLights().stream().filter(Entity::isActive).forEach(e -> {
            updateEntity(e, elapsed);
            e.update(elapsed);
            constrainsEntity(currentScene.getWorld(), e);
        });
        if (currentScene.getActiveCamera() != null) {
            currentScene.getActiveCamera().update(elapsed);
        }
        currentScene.update(null, elapsed);
    }

    public void updateEntity(Entity e, long elapsed) {
        if (e.getPhysicType().equals(PhysicType.DYNAMIC)) {
            e.setPosition((e.getPosition().getX() + (e.getVelocity().getX() * elapsed)), (e.getPosition().getY()
                    + ((e.getVelocity().getY() + (currentScene.getWorld().getGravity() * 0.01)) * elapsed)));
            // reduce velocity
            e.setVelocity((e.getVelocity().getX() * e.getMaterial().friction()),
                    (e.getVelocity().getY() * e.getMaterial().friction()));
        }
    }

    public void constrainsEntity(World w, Entity e) {
        if (e.getPosition().getX() < w.getPosition().getX()) {
            e.setPosition(w.getPosition().getX(), e.getPosition().getY());
            e.setVelocity(-(e.getVelocity().getX() * e.getMaterial().elasticity()), e.getVelocity().getY());
        } else if (e.getPosition().getX() + e.getWidth() > w.getPosition().getX() + w.getWidth()) {
            e.setPosition(w.getPosition().getX() + w.getWidth() - e.getWidth(), e.getPosition().getY());
            e.setVelocity(-(e.getVelocity().getX() * e.getMaterial().elasticity()), e.getVelocity().getY());
        }

        if (e.getPosition().getY() < w.getPosition().getY()) {
            e.setPosition(e.getPosition().getX(), w.getPosition().getY());
            e.setVelocity(e.getVelocity().getX(), -e.getVelocity().getY() * e.getMaterial().elasticity());
        } else if (e.getPosition().getY() + e.getHeight() > w.getPosition().getY() + w.getHeight()) {
            e.setPosition(e.getPosition().getX(), w.getPosition().getY() + w.getHeight() - e.getHeight());
            e.setVelocity(e.getVelocity().getX(), -e.getVelocity().getY() * e.getMaterial().elasticity());
        }

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

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE -> {
            exit = true;
        }
        case KeyEvent.VK_D -> {
            if (e.isControlDown()) {
                debug = (debug + 1) % 5;
            }
        }
        case KeyEvent.VK_PAUSE, KeyEvent.VK_P -> {
            pause = !pause;
        }
        default -> {
            // nothing to do in that case
        }
        }
    }

    public boolean isKeyPressed(int keyCode) {
        return this.keys[keyCode];
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
        String output = String.format("[%s] [%s] [%s] %s", timestamp, ll, clazz.getSimpleName(), formattedMsg);
        if (ll == LogLevel.ERROR) {
            System.err.println(output);
        } else {
            System.out.println(output);
        }
    }

}
