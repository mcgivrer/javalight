
public class Light extends Entity {
    private double intensity;
    private LightType lightType;

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

    public Light setLightType(LightType point) {
        this.lightType = point;
        return this;
    }

    public LightType getLightType() {
        return lightType;
    }
}
