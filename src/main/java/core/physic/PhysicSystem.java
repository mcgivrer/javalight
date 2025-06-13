package core.physic;

import core.App;
import core.entity.Entity;
import core.entity.World;
import core.scene.Scene;

public class PhysicSystem {

    private App app;

    public PhysicSystem(App app){
        this.app = app;
    }

     public void update(Scene scene, long elapsed) {

        app.time += elapsed;

        scene.getEntities().stream().filter(Entity::isActive).forEach(e -> {
            updateEntity(scene, e, elapsed);
            e.update(elapsed);
            constrainsEntity(scene.getWorld(), e);
        });
        scene.getLights().stream().filter(Entity::isActive).forEach(e -> {
            updateEntity(scene, e, elapsed);
            e.update(elapsed);
            constrainsEntity(scene.getWorld(), e);
        });
        if (scene.getActiveCamera() != null) {
            scene.getActiveCamera().update(elapsed);
        }
        scene.update(null, elapsed);
    }

    public void updateEntity(Scene scene, Entity e, long elapsed) {
        if (e.getPhysicType().equals(PhysicType.DYNAMIC)) {
            e.setPosition((e.getPosition().getX() + (e.getVelocity().getX() * elapsed)), (e.getPosition().getY()
                    + ((e.getVelocity().getY() + (scene.getWorld().getGravity() * 0.01)) * elapsed)));
            // reduce velocity
            e.setVelocity((e.getVelocity().getX() * e.getMaterial().friction()),
                    (e.getVelocity().getY() * e.getMaterial().friction()));
        }
    }

    public void constrainsEntity(World w, Entity e) {
        if (e.getPosition().getX() < w.getPosition().getX()) {
            e.setPosition(w.getPosition().getX(), e.getPosition().getY());
            e.setVelocity(-(e.getVelocity().getX() * e.getMaterial().elasticity()), e.getVelocity().getY());
        } else if (e.getPosition().getX() + e.getWidth() > w.getPosition().getX() + w.getWidth()) {
            e.setPosition(w.getPosition().getX() + w.getWidth() - e.getWidth(), e.getPosition().getY());
            e.setVelocity(-(e.getVelocity().getX() * e.getMaterial().elasticity()), e.getVelocity().getY());
        }

        if (e.getPosition().getY() < w.getPosition().getY()) {
            e.setPosition(e.getPosition().getX(), w.getPosition().getY());
            e.setVelocity(e.getVelocity().getX(), -e.getVelocity().getY() * e.getMaterial().elasticity());
        } else if (e.getPosition().getY() + e.getHeight() > w.getPosition().getY() + w.getHeight()) {
            e.setPosition(e.getPosition().getX(), w.getPosition().getY() + w.getHeight() - e.getHeight());
            e.setVelocity(e.getVelocity().getX(), -e.getVelocity().getY() * e.getMaterial().elasticity());
        }

    }
}
