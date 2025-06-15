package core.entity;

import java.awt.*;

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
        Stroke st = g.getStroke();
        g.setStroke(new BasicStroke(0.5f));
        g.setColor(Color.ORANGE.darker());
        g.drawRect((int) position.getX() + 20, (int) position.getY() + 20, (int) width - 40, (int) height - 40);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 8f));
        g.drawString(getName(), (int) position.getX() + 20, (int) (position.getY() + height - 20));
        g.setStroke(st);
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

