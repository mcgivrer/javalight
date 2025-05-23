import java.awt.*;
import java.awt.geom.Point2D;

public class Light extends Entity {
    private LightType lightType;
    private double intensity = 1.0, intensityDraw = 1.0;
    private double radius = 10;
    private double vibration = 0.0;
    private double direction = 0f;

    public Light(String name) {
        super(name);
        setPhysicType(PhysicType.STATIC);
    }

    public Light setIntensity(double i) {
        this.intensity = i;
        return this;
    }

    public Light setRadius(double i) {
        this.radius = i;
        return this;
    }

    public double getIntensity() {
        return intensity;
    }

    public Light setLightType(LightType point) {
        this.lightType = point;
        return this;
    }

    public LightType getLightType() {
        return lightType;
    }

    public double getRadius() {
        return radius;
    }


    @Override
    public void update(long elapsed) {
        intensityDraw = (intensity * 0.9) + (Math.random() * 0.1);
    }

    @Override
    public void draw(Graphics2D g2d) {
        float[] dist = {0.0f, 0.3f, 0.8f};
        Color[] colors = {
                getFillColor(), // centre lumineux, légèrement jaune
                Utils.setAlpha(getFillColor(), (float) (intensityDraw)),  // bord transparent
                Utils.setAlpha(getFillColor(), (float) (intensityDraw * 0.2)),  // bord transparent
        };
        RadialGradientPaint paint = new RadialGradientPaint(position, (float) radius, dist, colors);
        Composite oldComposite = g2d.getComposite();
        g2d.setPaint(paint);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.fillOval(
                (int) (position.getX() - radius + Math.random() * vibration),
                (int) (position.getY() - radius + Math.random() * vibration),
                (int) radius * 2,
                (int) radius * 2);
        g2d.setComposite(oldComposite);
    }

    public Light setVibration(double v) {
        this.vibration = v;
        return this;
    }

    public double getIntensityDraw() {
        return intensityDraw;
    }

    public double getVibration() {
        return vibration;
    }

    public double getDirection() {
        return direction;
    }

    public Light setDirection(double d) {
        this.direction = d;
        return this;
    }
}
