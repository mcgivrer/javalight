import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

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
    private long time = 0;

    private World world = new World("earth", 320, 200);

    private final BufferedImage renderBuffer = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);

    private final List<Entity> entities = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private Camera currentCamera;

    public App() {
        System.out.printf("Welcome to %s (%s) !%n", messages.getString("app.name"), messages.getString("app.version"));
    }

    public void initConfig(String configFilePath) {
        try {
            config.load(this.getClass().getResourceAsStream(configFilePath));
        } catch (Exception e) {
            System.err.printf("Unable to read configuration file %s:%s", configFilePath, e.getMessage());
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
        long startTime = 0, endTime = 0, elapsed = 0;
        initScene();
        endTime = System.currentTimeMillis();
        do {

            startTime = endTime;
            if (!pause) {
                update(elapsed);
                render();
            }
            try {
                Thread.sleep((1000 / (FPS)) - elapsed > 0 ? (1000 / (FPS)) - elapsed : 1);
            } catch (Exception e) {
                // something goes wrong in the matrix
            }

            endTime = System.currentTimeMillis();
            elapsed = endTime - startTime;

        } while (!exit);
    }


    private void initScene() {
        addEntity(world);
        Entity player = new Entity("player")
                .setPosition(20, 20)
                .setSize(16, 16)
                .setEdgeColor(Color.RED)
                .setFillColor(Color.RED.darker())
                .setMaterial(new Material("body", 0.92, 0.98, 1.0));
        addEntity(player);
        Random rand = new Random(1234);
        for (int i = 0; i < 20; i++) {
            addEntity(new Entity("enemy_%d".formatted(i))
                    .setPosition(rand.nextDouble(world.getWidth()), rand.nextDouble(world.getHeight()))
                    .setSize(8, 8)
                    .setEdgeColor(Color.ORANGE)
                    .setFillColor(Color.ORANGE.darker())
                    .setMaterial(new Material("enemy", 1.0, 1.0, 1.0))
                    .setVelocity(-0.2 + rand.nextDouble(0.4), -0.2 + rand.nextDouble(0.4))
            );
        }

        addLight(new Light("light-sun")
                .setLightType(LightType.POINT)
                .setIntensity(0.3)
                .setRadius(120)
                .setPosition((world.getWidth()) / 5, (world.getHeight() - 40) / 6)
                .setFillColor(new Color(0, 255, 230)));
        addLight(new Light("light-directional")
                .setLightType(LightType.DIRECTIONAL)
                .setIntensity(0.3)
                .setSize(80, world.getHeight())
                .setPosition((world.getWidth()) * 2 / 5, 0)
                .setFillColor(new Color(255, 255, 230)));
        addLight(new Light("light-spot")
                .setLightType(LightType.SPOT)
                .setIntensity(0.6)
                .setRadius(60)
                .setDirection(Math.PI / 4.0)
                .setPosition((world.getWidth() * 3 / 5) / 2, world.getHeight())
                .setSize(50, world.getHeight())
                .setFillColor(new Color(255, 255, 250)));
        addLight(new Light("light-area")
                .setLightType(LightType.AREA)
                .setIntensity(0.3)
                .setPosition(0, 0)
                .setSize(world.getWidth(), world.getHeight())
                .setFillColor(new Color(255, 100, 30)));

        setCamera(new Camera("cam01").setTarget(player).setSize(320, 200));
    }

    private void addLight(Light light) {
        lights.add(light);
    }

    public void setCamera(Camera cam) {
        this.currentCamera = cam;
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    private void update(long elapsed) {
        time += elapsed;

        Entity player = getEntity("player");
        double step = 0.1;
        if (isKeyPressed(KeyEvent.VK_UP)) {
            player.setVelocity(player.getVelocity().getX(), -step * 6);
        }
        if (isKeyPressed(KeyEvent.VK_DOWN)) {
            player.setVelocity(player.getVelocity().getX(), +step);
        }
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            player.setVelocity(-step, player.getVelocity().getY());
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.setVelocity(+step, player.getVelocity().getY());
        }

        entities.stream().filter(Entity::isActive).forEach(e -> {
            updateEntity(e, elapsed);
            e.update(elapsed);
            constrainsEntity(world, e);
        });
        lights.stream().filter(Entity::isActive).forEach(e -> {
            updateEntity(e, elapsed);
            e.update(elapsed);
            constrainsEntity(world, e);
        });
        if (currentCamera != null) {
            currentCamera.update(elapsed);
        }
    }

    public void updateEntity(Entity e, long elapsed) {
        if (e.getPhysicType().equals(PhysicType.DYNAMIC)) {
            e.setPosition(
                    (e.getPosition().getX() + (e.getVelocity().getX() * elapsed)),
                    (e.getPosition().getY() + ((e.getVelocity().getY() + (world.getGravity() * 0.01)) * elapsed)));
            //reduce velocity
            e.setVelocity(
                    (e.getVelocity().getX() * e.getMaterial().friction()),
                    (e.getVelocity().getY() * e.getMaterial().friction()));
        }
    }

    public void constrainsEntity(World w, Entity e) {
        if (e.getPosition().getX() < w.getPosition().getX()) {
            e.setPosition(w.getPosition().getX(), e.getPosition().getY());
            e.setVelocity(
                    -(e.getVelocity().getX() * e.getMaterial().elasticity()),
                    e.getVelocity().getY());
        } else if (e.getPosition().getX() + e.getWidth() > w.getPosition().getX() + w.getWidth()) {
            e.setPosition(w.getPosition().getX() + w.getWidth() - e.getWidth(), e.getPosition().getY());
            e.setVelocity(
                    -(e.getVelocity().getX() * e.getMaterial().elasticity()),
                    e.getVelocity().getY());
        }

        if (e.getPosition().getY() < w.getPosition().getY()) {
            e.setPosition(e.getPosition().getX(), w.getPosition().getY());
            e.setVelocity(
                    e.getVelocity().getX(),
                    -e.getVelocity().getY() * e.getMaterial().elasticity());
        } else if (e.getPosition().getY() + e.getHeight() > w.getPosition().getY() + w.getHeight()) {
            e.setPosition(e.getPosition().getX(), w.getPosition().getY() + w.getHeight() - e.getHeight());
            e.setVelocity(
                    e.getVelocity().getX(),
                    -e.getVelocity().getY() * e.getMaterial().elasticity());
        }

    }

    private void render() {
        Graphics2D g = renderBuffer.createGraphics();
        //clear buffer
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        // configure rendering
        g.setRenderingHints(
                Map.of(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                        RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

        // draw all entities.
        entities.stream()
                .filter(Entity::isActive)
                .forEach(e -> {
                    if (currentCamera != null) {
                        g.translate(-currentCamera.position.getX(), -currentCamera.position.getY());
                    }

                    drawEntity(g, e);
                    e.draw(g);
                    if (currentCamera != null) {
                        g.translate(currentCamera.position.getX(), currentCamera.position.getY());
                    }
                });

        // rendering lights
        lights.stream()
                .filter(Entity::isActive)
                .forEach(l -> {
                    if (currentCamera != null) {
                        g.translate(-currentCamera.position.getX(), -currentCamera.position.getY());
                    }
                    drawLight(g, l);
                    if (currentCamera != null) {
                        g.translate(currentCamera.position.getX(), currentCamera.position.getY());
                    }
                });


        g.dispose();

        BufferStrategy bs = window.getBufferStrategy();
        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();

        g2.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

        int GAME_WIDTH = renderBuffer.getWidth();
        int GAME_HEIGHT = renderBuffer.getHeight();
        float TARGET_RATIO = GAME_WIDTH / (float) GAME_HEIGHT;
        // Calcul du viewport centré et respectant le ratio
        int panelWidth = window.getWidth();
        int panelHeight = window.getHeight();

        int targetWidth = panelWidth;
        int targetHeight = (int) (targetWidth / TARGET_RATIO);

        if (targetHeight > panelHeight) {
            targetHeight = panelHeight;
            targetWidth = (int) (targetHeight * TARGET_RATIO);
        }

        int xOffset = (panelWidth - targetWidth) / 2;
        int yOffset = (panelHeight - targetHeight) / 2;

        // Dessin du buffer dans la fenêtre, centré et redimensionné
        g2.drawImage(renderBuffer, xOffset, yOffset, targetWidth, targetHeight, null);

        // draw debug information
        g2.setColor(Color.ORANGE);
        g2.setFont(g.getFont().deriveFont(12.0f));
        g2.drawString(getFormatedTime(time), 20, window.getHeight() - 20);
        g2.dispose();
        bs.show();
    }

    private void drawLight(Graphics2D g, Light e) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (e.getIntensity())));
        switch (e.getLightType()) {
            case LightType.POINT -> {
                float[] dist = {0.0f, 0.3f, 0.8f};
                Color[] colors = {
                        e.getFillColor(), // centre lumineux, légèrement jaune
                        Utils.setAlpha(e.getFillColor(), (float) (e.getIntensityDraw())),  // bord transparent
                        Utils.setAlpha(e.getFillColor(), (float) (e.getIntensityDraw() * 0.2)),  // bord transparent
                };
                RadialGradientPaint paint = new RadialGradientPaint(e.getPosition(), (float) e.getRadius(), dist, colors);
                Composite oldComposite = g.getComposite();
                g.setPaint(paint);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g.fillOval(
                        (int) (e.getPosition().getX() - e.getRadius() + Math.random() * e.getVibration()),
                        (int) (e.getPosition().getY() - e.getRadius() + Math.random() * e.getVibration()),
                        (int) e.getRadius() * 2,
                        (int) e.getRadius() * 2);
                g.setComposite(oldComposite);
            }
            case LightType.DIRECTIONAL -> {

                g.setColor(e.getFillColor());
                g.fill(
                        new Rectangle2D.Double(e.getPosition().getX(), e.getPosition().getY(), e.getWidth(), e.getHeight()));
            }
            case LightType.SPOT -> {

                g.setColor(Utils.setAlpha(e.getFillColor(), (float) e.getIntensityDraw()));
                g.rotate(e.getDirection(), e.getPosition().getX(), e.getPosition().getY());
                Polygon p = new Polygon(
                        new int[]{(int) e.getPosition().getX(), (int) e.getPosition().getX() + (int) e.getWidth() / 2,
                                (int) e.getPosition().getX() + (int) e.getWidth()},
                        new int[]{(int) e.getPosition().getY(), (int) e.getPosition().getY() + (int) e.getHeight(),
                                (int) e.getPosition().getY()},
                        3);
                g.fill(p);
                g.rotate(-e.getDirection(), e.getPosition().getX(), e.getPosition().getY());
            }
            case LightType.AREA -> {

                g.setColor(e.getFillColor());
                g.fill(
                        new Rectangle2D.Double(e.getPosition().getX(), e.getPosition().getY(), e.getWidth(), e.getHeight()));
            }
        }
    }

    private String getFormatedTime(long time) {
        return "%02d:%02d:%02d".formatted(((time / 1000) * 3600) % 24, ((time / 1000) / 60) % 60, ((time / 1000) % 60));
    }

    private void drawEntity(Graphics2D g, Entity e) {
        if (e.getFillColor() != null) {
            g.setColor(e.getFillColor());
            g.fillRect((int) e.getPosition().getX(), (int) e.getPosition().getY(), (int) e.getWidth(), (int) e.getHeight());
        }
        if (e.getEdgeColor() != null) {
            g.setColor(e.getEdgeColor());
            g.drawRect((int) e.getPosition().getX(), (int) e.getPosition().getY(), (int) e.getWidth(), (int) e.getHeight());
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
