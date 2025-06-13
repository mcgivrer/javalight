package core.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import core.physic.PhysicType;

public class World extends Entity {
    private double gravity = 9.81f;

    public World(String name, int w, int h) {
        super(name);
        setPosition(0, 0);
        setSize(w, h);
        setFillColor(null);
        setEdgeColor(Color.DARK_GRAY);
        setPhysicType(PhysicType.STATIC);
    }

    public World setGravity(double gravity) {
        this.gravity = gravity;
        return this;
    }

    public double getGravity() {
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
