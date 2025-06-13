package core.entity;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import core.physic.Material;
import core.physic.PhysicType;
import core.scene.Scene;


public class Entity {
    private static long index = 0;
    protected long id = index++;

    protected String name = "entity_%d".formatted(id);

    protected Point2D position = new Point2D.Double(0, 0);
    protected Point2D velocity = new Point2D.Double(0, 0);
    protected double width = 16;
    protected double height = 16;

    protected PhysicType physicType = PhysicType.DYNAMIC;
    protected Material material = Material.DEFAULT;
    protected boolean active = true;

    private Color edgeColor = Color.WHITE;
    private Color fillColor = Color.BLUE;


    public Entity() {
    }

    public Entity(String name) {
        this.name = name;
    }

    public <T extends Entity> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public <T extends Entity> T setPosition(double x, double y) {
        this.position = new Point2D.Double(x, y);
        return (T) this;
    }

    public <T extends Entity> T setVelocity(double dx, double dy) {
        this.velocity = new Point2D.Double(dx, dy);
        return (T) this;
    }

    public <T extends Entity> T setSize(double w, double h) {
        this.width = w;
        this.height = h;
        return (T) this;
    }

    public <T extends Entity> T setEdgeColor(Color ec) {
        this.edgeColor = ec;
        return (T) this;
    }

    public <T extends Entity> T setFillColor(Color fc) {
        this.fillColor = fc;
        return (T) this;
    }

    public <T extends Entity> T setPhysicType(PhysicType physicType) {
        this.physicType = physicType;
        return (T) this;
    }

    public <T extends Entity> T setActive(boolean a) {
        this.active = a;
        return (T) this;
    }

    public <T extends Entity> T setMaterial(Material m) {
        this.material = m;
        return (T) this;
    }

    public String getName() {
        return this.name;
    }

    public Point2D getPosition() {
        return this.position;
    }

    public Point2D getVelocity() {
        return this.velocity;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public Color getEdgeColor() {
        return this.edgeColor;
    }

    public Color getFillColor() {
        return this.fillColor;
    }

    public PhysicType getPhysicType() {
        return physicType;
    }

    public boolean isActive() {
        return this.active;
    }

    public void draw(Graphics2D g) {

    }

    public void drawDebug(Scene scene, Graphics2D g, int debug) {
        if (debug > 0) {
            g.setColor(Color.ORANGE);
            g.draw(new Rectangle2D.Double(position.getX(), position.getY(), width, height));
            World world = scene.getWorld();
            if (physicType != PhysicType.STATIC) {
                // display velocity vector
                g.setColor(Color.YELLOW);
                g.drawRect((int) (position.getX() + width / 2), (int) (position.getY() + height / 2), 1, 1);
                g.drawLine(
                        (int) (position.getX() + width / 2), (int) (position.getY() + height / 2),
                        (int) ((position.getX() + width / 2) + velocity.getY() * 100), (int) ((position.getY() + height / 2) + velocity.getY() * 100));
                // display world gravity
                g.setColor(Color.CYAN);
                g.drawLine(
                        (int) (position.getX() + width / 2), (int) (position.getY() + height / 2),
                        (int) ((position.getX() + width / 2) + world.getGravity().getX() * 5),
                        (int) ((position.getY() + height / 2) + world.getGravity().getY() * 5));
            }
            if (debug > 1) {
                //getBehaviors().stream().forEach(b -> b.drawDebug(this, g));
                if (debug > 2) {
                    g.setColor(Color.ORANGE);
                    g.setFont(g.getFont().deriveFont(Font.BOLD, 9f));
                    g.drawString(this.getName(), (int) (position.getX() + width + 2), (int) (position.getY() + 9));
                    if (debug > 3) {
                        g.drawString("p(%3.2f,%3.2f)".formatted(position.getX(), position.getY()), (int) (position.getX() + width + 2), (int) (position.getY() + 18));
                        g.drawString("v(%3.2f,%3.2f)".formatted(velocity.getX(), velocity.getY()), (int) (position.getX() + width + 2), (int) (position.getY() + 27));
                        if (debug > 4) {
                            //g.drawString("mass: %3.1f", mass), (int) (position.getX() + width + 2), (int) (position.getY() + 36));
                            g.drawString("physicType: %s".formatted(physicType), (int) (position.getX() + width + 2), (int) (position.getY() + 45));
                            g.drawString("material: %s".formatted(material.name()), (int) (position.getX() + width + 2), (int) (position.getY() + 54));
                        }
                    }
                }
            }
        }

    }

    public void update(long elapsed) {

    }

    public Material getMaterial() {
        return material;
    }
}
