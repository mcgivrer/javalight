import java.awt.geom.Point2D;

public class Light extends Entity {
    private double intensity;

    public Light(String name) {
        super(name);
    }

    public <T extends Entity> T setIntensity(double i) {
        this.intensity = i;
        return (T) this;
    }

    public double getIntensity() {
        return intensity;
    }
}
