package core.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import core.physic.PhysicType;

public class World extends Entity {
    private Point2D gravity = new Point2D.Double(0,9.81);

    public World(String name, int w, int h) {
        super(name);
        setPosition(0, 0);
        setSize(w, h);
        setFillColor(null);
        setEdgeColor(Color.DARK_GRAY);
        setPhysicType(PhysicType.STATIC);
    }

    public World setGravity(Point2D gravity) {
        this.gravity = gravity;
        return this;
    }

    public Point2D  getGravity() {
        return this.gravity;
    }

    @Override
    public void draw(Graphics2D g) {
        for (int ix = 0; ix < getWidth(); ix += 8) {
            for (int iy = 0; iy < getHeight(); iy += 8) {
                g.drawLine(
                        (int) position.getX() + ix, (int) position.getY() + iy,
                        (int) position.getX() + ix, (int) position.getY() + iy);
            }
        }
    }
}
