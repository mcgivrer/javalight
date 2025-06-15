package core.entity;

import core.io.InputHandler;
import core.physic.PhysicType;
import core.scene.Scene;

import java.awt.*;

public class MouseCursor extends Entity {

    Polygon mouse = new Polygon(new int[]{0, 8, 0}, new int[]{0, 0, 8}, 3);

    public MouseCursor(String name) {
        super(name);
        setSize(8, 8);
        setEdgeColor(null);
        setFillColor(null);
        setPhysicType(PhysicType.STATIC);
    }

    @Override
    public void update(long elapsed) {
        Point p = InputHandler.getMousePosition();
        Scene s = getScene();
        if (s.getActiveCamera() != null) {
            p.setLocation(
                    p.getX() - (s.getActiveCamera().position.getX() + s.getActiveCamera().width),
                    p.getY() - (s.getActiveCamera().position.getY() + s.getActiveCamera().height));
        }
        this.position.setLocation(p.getX(), p.getY());
    }

    @Override
    public void draw(Graphics2D g) {
        g.translate((int) position.getX(), (int) position.getY());
        g.setColor(Color.WHITE);
        g.fill(mouse);
        g.setColor(Color.BLACK);
        g.draw(mouse);
        g.translate(-(int) position.getX(), -(int) position.getY());

    }
}
