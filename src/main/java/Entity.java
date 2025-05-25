import java.awt.*;
import java.awt.geom.Point2D;


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

    public void update(long elapsed) {

    }

    public Material getMaterial() {
        return material;
    }
}
