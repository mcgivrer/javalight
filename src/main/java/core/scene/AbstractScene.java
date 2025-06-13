package core.scene;

import java.util.ArrayList;
import java.util.List;

import core.entity.Camera;
import core.entity.Entity;
import core.entity.Light;
import core.entity.World;

public class AbstractScene {

    protected final List<Entity> entities = new ArrayList<>();
    protected final List<Light> lights = new ArrayList<>();

    protected List<Camera> cameras = new ArrayList<>();
    protected Camera activeCamera;

    protected World world = new World("earth", 320, 200);

    public void addLight(Light light) {
        lights.add(light);
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setActiveCamera(Camera camera) {
        if (!cameras.contains(camera)) {
            cameras.add(camera);
        }
        activeCamera = camera;
    }

    public <T extends Entity> T getEntity(String name) {
        return (T) entities.stream().filter(e -> e.getName().equals(name)).findFirst().get();
    }

    public World getWorld() {
        return world;
    }

    public Camera getActiveCamera() {
        return activeCamera;
    }
}
