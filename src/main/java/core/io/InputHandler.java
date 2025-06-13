package core.io;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import core.App;
import core.scene.Scene;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    private static boolean[] keys = new boolean[1024];
    private App app;

    private static Point mousePosition;
    private static boolean[] buttons;

    public InputHandler(App app) {
        this.app = app;
        mousePosition = new Point(0, 0);
        buttons = new boolean[MouseInfo.getNumberOfButtons()];
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

        buttons[e.getButton()] = true;
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        buttons[e.getButton()] = false;
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

        Scene scene = app.getCurrentScene();
        if (scene != null) {
            scene.onKeyPressed(app, e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                app.setExit(true);
            }
            case KeyEvent.VK_D -> {
                if (e.isControlDown()) {
                    app.setDebug((app.getDebug() + 1) % 5);
                }
            }
            case KeyEvent.VK_PAUSE, KeyEvent.VK_P -> {
                app.setPause(!app.isPause());
            }
            default -> {
                // nothing to do in that case
            }
        }

        Scene scene = app.getCurrentScene();
        if (scene != null) {
            scene.onKeyReleased(app, e.getKeyCode());
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    public static boolean isMouButtonPressed(int mb) {
        return buttons[mb];
    }

    public static Point getMousePosition() {
        return mousePosition;
    }

}
