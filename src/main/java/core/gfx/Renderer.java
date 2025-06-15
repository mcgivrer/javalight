package core.gfx;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import core.App;
import core.entity.Entity;
import core.entity.Light;
import core.entity.LightType;
import core.scene.Scene;
import core.utils.Utils;

public class Renderer {

    private App app;

    private JFrame window;
    private final BufferedImage renderBuffer;
    private boolean displayHelp;

    public Renderer(App app) {
        this.app = app;
        Dimension bufferSize = app.getConfiguration().get("app.gfx.rendering.buffer.size", new Dimension(320, 200));
        renderBuffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB);
    }

    public void prepareWindow() {
        window = new JFrame(App.messages.getString("app.name"));
        window.setPreferredSize(app.getConfiguration().get("app.window.size", new Dimension(640, 400)));
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
                app.setExit(true);
                System.exit(0);
            }
        });
        window.addKeyListener(app.getInputHandler());
        window.addMouseListener((MouseListener) app.getInputHandler());
        window.addMouseMotionListener((MouseMotionListener) app.getInputHandler());
        window.pack();
        window.setBackground(Color.BLACK);
        window.setVisible(true);
        window.createBufferStrategy(3);
        window.requestFocus();
    }

    public void draw(Scene scene) {
        Graphics2D g = renderBuffer.createGraphics();
        // clear buffer
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        // configure rendering
        g.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED));

        // draw all entities.
        scene.getEntities().stream()
                .filter(e -> e.isActive() && !(e instanceof Light l))
                .forEach(e -> {
                    if (scene.getActiveCamera() != null) {
                        g.translate(-scene.getActiveCamera().getPosition().getX(),
                                -scene.getActiveCamera().getPosition().getY());
                    }

                    drawEntity(g, e);
                    e.draw(g);
                    if (scene.getActiveCamera() != null) {
                        g.translate(scene.getActiveCamera().getPosition().getX(), scene.getActiveCamera().getPosition().getY());
                    }
                });
        if (scene.getActiveCamera() != null) {
            g.translate(-scene.getActiveCamera().getPosition().getX(),
                    -scene.getActiveCamera().getPosition().getY());
        }
        scene.getActiveCamera().draw(g);
        if (scene.getActiveCamera() != null) {
            g.translate(scene.getActiveCamera().getPosition().getX(), scene.getActiveCamera().getPosition().getY());
        }
        // rendering lights
        scene.getEntities().stream()
                .filter(e -> e.isActive() && e instanceof Light l)
                .forEach(l -> {
                    if (scene.getActiveCamera() != null) {
                        g.translate(-scene.getActiveCamera().getPosition().getX(),
                                -scene.getActiveCamera().getPosition().getY());
                    }
                    drawLight(g, (Light) l);
                    if (scene.getActiveCamera() != null) {
                        g.translate(scene.getActiveCamera().getPosition().getX(), scene.getActiveCamera().getPosition().getY());
                    }
                });

        g.dispose();
        drawToWindow(scene);
    }

    private void drawToWindow(Scene scene) {
        BufferStrategy bs = window.getBufferStrategy();
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
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
        g.drawImage(renderBuffer, xOffset, yOffset, targetWidth, targetHeight, null);
        if (displayHelp) {
            displayHelp(g);
        }
        if (app.debug > 0) {
            drawDebugInfo(g, scene);
        }
        g.dispose();
        bs.show();
    }

    private void drawDebugInfo(Graphics2D g, Scene scene) {
        // draw debug information
        g.setColor(Color.ORANGE);
        g.setFont(g.getFont().deriveFont(12.0f));
        g.drawString(
                "[ debug:%d | time:%s | mode:%s | update:%s | g:%.2f]".formatted(app.debug,
                        Utils.getFormatedTime(app.getGameTime()),
                        app.mode,
                        app.pause ? "PAUSED" : "RUNNING",
                        scene.getWorld().getGravity().getY()),
                20, window.getHeight() - 20);
    }

    private void drawLight(Graphics2D g, Light e) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (e.getIntensity())));
        switch (e.getLightType()) {
            case LightType.POINT -> {
                float[] dist = {0.0f, 0.3f, 0.8f};
                Color[] colors = {e.getFillColor(), // centre lumineux, légèrement jaune
                        Utils.setAlpha(e.getFillColor(), (float) (e.getIntensityDraw())), // bord transparent
                        Utils.setAlpha(e.getFillColor(), (float) (e.getIntensityDraw() * 0.2)), // bord transparent
                };
                RadialGradientPaint paint = new RadialGradientPaint(e.getPosition(), (float) e.getRadius(), dist, colors);
                Composite oldComposite = g.getComposite();
                g.setPaint(paint);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g.fillOval((int) (e.getPosition().getX() - e.getRadius() + Math.random() * e.getVibration()),
                        (int) (e.getPosition().getY() - e.getRadius() + Math.random() * e.getVibration()),
                        (int) e.getRadius() * 2, (int) e.getRadius() * 2);
                g.setComposite(oldComposite);
            }
            case LightType.DIRECTIONAL -> {

                g.setColor(e.getFillColor());
                g.fill(new Rectangle2D.Double(e.getPosition().getX(), e.getPosition().getY(), e.getWidth(), e.getHeight()));
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
                g.fill(new Rectangle2D.Double(e.getPosition().getX(), e.getPosition().getY(), e.getWidth(), e.getHeight()));
            }
        }
    }

    private void drawEntity(Graphics2D g, Entity e) {
        if (e.getFillColor() != null) {
            g.setColor(e.getFillColor());
            g.fillRect((int) e.getPosition().getX(), (int) e.getPosition().getY(), (int) e.getWidth(),
                    (int) e.getHeight());
        }
        if (e.getEdgeColor() != null) {
            g.setColor(e.getEdgeColor());
            g.drawRect((int) e.getPosition().getX(), (int) e.getPosition().getY(), (int) e.getWidth(),
                    (int) e.getHeight());
        }
    }

    private void displayHelp(Graphics g) {
        String[] helpTextLines = App.messages.getString("app.help.text").split("\n");
        int iy = window.getHeight() - 40;
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 12.0f));
        int lineHeight = g.getFontMetrics().getHeight();
        for (String l : helpTextLines) {
            g.setColor(Color.BLACK);
            g.drawString(l, 20 + 1, iy + 1);
            g.setColor(Color.WHITE);
            g.drawString(l, 20, iy);
            iy -= lineHeight;
        }
    }

    public void dispose() {
        if (window != null) {
            window.dispose();
        }
    }

    public void setDisplayHelp(boolean b) {
        this.displayHelp = b;
    }

    public BufferedImage getRenderBuffer() {
        return renderBuffer;
    }

    public Window getWindow() {
        return window;
    }
}
