package core.entity;

import java.awt.Color;
import java.awt.Graphics2D;

public class Camera extends Entity {
    private Entity target;

    public Camera(String name) {
        super(name);
        setEdgeColor(null);
        setFillColor(null);
    }

    public <T extends Entity> T setTarget(Entity target) {
        this.target = target;
        return (T) this;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.GREEN.darker());
        g.drawRect((int) position.getX() + 20, (int) position.getY() + 20, (int) width - 40, (int) height - 40);
    }

    @Override
    public void update(long elapsed) {
        if (target != null) {
            setPosition(
                    target.position.getX() - (target.getWidth() + getWidth()) / 2,
                    target.position.getY() - (target.getHeight() + getHeight()) / 2);
        }
    }
}

