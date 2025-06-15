package core.scene;

import java.util.List;

import core.App;
import core.entity.Camera;
import core.entity.Entity;
import core.entity.Light;
import core.entity.World;
import core.gfx.Renderer;

public interface Scene {
    default void initialize(App app) {
    }

    default void create(App app) {
    }

    default void input(App app) {
    }

    default void update(App app, long elapsed) {
    }

    default void render(App app, Renderer r) {
    }

    default void dispose(App app) {
    }

    default void onKeyPressed(App app, int keyCode) {
    }

    default void onKeyReleased(App app, int key) {
    }

    List<Entity> getEntities();

    <T extends Entity> T getEntity(String string);

    World getWorld();

    Camera getActiveCamera();

}
