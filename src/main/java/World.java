import java.awt.*;

public class World extends Entity {
    private float gravity = 9.81f;

    public World(String name, int w, int h) {
        super(name);
        setSize(w, h);
        setFillColor(null);
    }

    public float getGravity() {
        return this.gravity;
    }
}
