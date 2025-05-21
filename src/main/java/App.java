import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.event.*;
import java.util.ResourceBundle;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

public class App implements KeyListener {

    public enum RunningMode {
        DEV,
        TEST,
        PROD;
    }

    public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    public static Properties config = new Properties();
    private static final long FPS = 60;
    private JFrame window;

    private boolean[] keys = new boolean[1024];

    private int debug = 0;
    private RunningMode mode = RunningMode.PROD;

    private boolean exit = false;
    private boolean pause = false;
    private long time = System.currentTimeMillis();

    private World world = new World("earth", 640, 400);

    private List<Entity> entities = new ArrayList<>();

    public App() {
        System.out.printf(
                "Welcome to %s (%s) !%n", messages.getString("app.name"),
                messages.getString("app.version"));
    }

    public void initConfig(String configFilePath) {
        try {
            config.load(this.getClass().getResourceAsStream(configFilePath));
        } catch (Exception e) {
            System.err.printf("Unable to read configuration file %s:%s",
                    configFilePath,
                    e.getMessage());
        }
    }

    public void run(String[] args) {
        initConfig("config.properties");
        parseArgs(args);
        extractConfigValues();
        prepareWindow();
        System.out.println("RUN !");
        loop();
        dispose();
    }

    private void prepareWindow() {
        window = new JFrame(messages.getString("app.name"));
        window.setPreferredSize(new Dimension(720, 460));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit = true;
                System.exit(0);
            }
        });
        window.addKeyListener(this);
        window.pack();
        window.setBackground(Color.BLACK);
        window.setVisible(true);
        window.createBufferStrategy(3);
        window.requestFocus();
    }

    public void parseArgs(String[] args) {
        System.out.println("Parse command line arguments...");
        for (String arg : args) {
            String[] keyVal = arg.split("=");
            config.setProperty(keyVal[0], keyVal[1]);
            System.out.printf(" |_ Override config:%s=%s%n", keyVal[0], keyVal[1]);
        }
    }

    private void extractConfigValues() {
        System.out.println("Read configuration...");
        for (String key : config.stringPropertyNames()) {
            switch (key) {
                case "app.debug", "debug", "d" -> {
                    debug = Integer.parseInt(config.getProperty(key, "0"));
                    System.out.printf("=> debug level set to %d%n", debug);
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
            default -> System.err.printf("Unknown configuration key %s%n", key);
        }
        return null;
    }

    private void loop() {
        initScene();
        do {
            if (!pause) {
                update();
                render();
            }
            try {
                Thread.sleep(1000 / (FPS * 2));
            } catch (Exception e) {
                // something goes wrong in the matrix
            }
        } while (!exit);
    }


    private void initScene() {
        addEntity(world);
        addEntity(new Entity("player")
                .setPosition(320, 120)
                .setSize(16, 16)
                .setEdgeColor(Color.RED)
                .setFillColor(Color.RED.darker())
        );
    }

    private void addEntity(Entity e) {
        entities.add(e);
    }

    private void update() {
        time += 1000 / FPS;

        Entity player = getEntity("player");
        int step = 1;
        if (isKeyPressed(KeyEvent.VK_UP)) {
            player.setPosition(player.getPosition().x, player.getPosition().y - step);
        }
        if (isKeyPressed(KeyEvent.VK_DOWN)) {
            player.setPosition(player.getPosition().x, player.getPosition().y + step);
        }
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            player.setPosition(player.getPosition().x - step, player.getPosition().y);
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.setPosition(player.getPosition().x + step, player.getPosition().y);
        }

        for (Entity e : entities) {
            updateEntity(e);
            containsEntity(world, e);
        }
    }

    public void updateEntity(Entity e) {

    }

    public void containsEntity(World w, Entity e) {
        if (e.getPosition().x < w.getPosition().x) {
            e.setPosition(w.getPosition().x, e.getPosition().y);
        }
        if (e.getPosition().y < w.getPosition().y) {
            e.setPosition(e.getPosition().x, w.getPosition().y);
        }

        if (e.getPosition().x + e.getWidth() > w.getPosition().x + w.getWidth()) {
            e.setPosition(w.getPosition().x + w.getWidth() - e.getWidth(), e.getPosition().y);
        }
        if (e.getPosition().y + e.getHeight() > w.getPosition().y + w.getHeight()) {
            e.setPosition(e.getPosition().x, w.getPosition().y + w.getHeight() - e.getHeight());
        }

    }

    private void render() {
        BufferStrategy bs = window.getBufferStrategy();
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        g.setColor(Color.WHITE);
        g.drawString("time is flying %d".formatted(time), 320, 100);
        for (Entity e : entities) {
            drawEntity(g, e);
        }
        g.dispose();
        bs.show();
    }

    private void drawEntity(Graphics2D g, Entity e) {
        if (e.getFillColor() != null) {
            g.setColor(e.getFillColor());
            g.fillRect(e.getPosition().x, e.getPosition().y, e.getWidth(), e.getHeight());
        }
        if (e.getEdgeColor() != null) {
            g.setColor(e.getEdgeColor());
            g.drawRect(e.getPosition().x, e.getPosition().y, e.getWidth(), e.getHeight());
        }
    }

    public Entity getEntity(String name) {
        return entities.stream().filter(e -> e.getName().equals(name)).findFirst().get();
    }

    private void dispose() {
        if (window != null) {
            window.dispose();
        }
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
}
