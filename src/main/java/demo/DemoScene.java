package demo;

import static core.io.InputHandler.*;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Random;

import core.App;
import core.entity.Camera;
import core.entity.Entity;
import core.entity.Light;
import core.entity.LightType;
import core.physic.Material;
import core.scene.AbstractScene;
import core.scene.Scene;

public class DemoScene extends AbstractScene implements Scene {

    public DemoScene() {

    }

    @Override
    public void initialize(App app) {

    }

    @Override
    public void create(App app) {
        addEntity(world);
        Entity player = new Entity("player").setPosition(20, 20).setSize(16, 16).setEdgeColor(Color.RED)
                .setFillColor(Color.RED.darker()).setMaterial(new Material("body", 0.92, 0.98, 1.0));
        addEntity(player);
        Random rand = new Random(1234);
        for (int i = 0; i < 20; i++) {
            addEntity(new Entity("enemy_%d".formatted(i))
                    .setPosition(rand.nextDouble(world.getWidth()), rand.nextDouble(world.getHeight())).setSize(8, 8)
                    .setEdgeColor(Color.ORANGE).setFillColor(Color.ORANGE.darker())
                    .setMaterial(new Material("enemy", 1.0, 1.0, 1.0))
                    .setVelocity(-0.2 + rand.nextDouble(0.4), -0.2 + rand.nextDouble(0.4)));
        }

        addLight(new Light("light-sun").setLightType(LightType.POINT).setIntensity(0.3).setRadius(120)
                .setPosition((world.getWidth()) / 5, (world.getHeight() - 40) / 6)
                .setFillColor(new Color(0, 255, 230)));
        addLight(new Light("light-directional")
                .setLightType(LightType.DIRECTIONAL)
                .setIntensity(0.3)
                .setVibration(-1)
                .setSize(80, world.getHeight()).setPosition((world.getWidth()) * 2 / 5, 0)
                .setFillColor(new Color(255, 255, 230)));
        addLight(new Light("light-spot").setLightType(LightType.SPOT).setIntensity(0.6).setRadius(80)
                .setDirection(-Math.PI / 4.0).setPosition(world.getWidth(), world.getHeight())
                .setSize(50, world.getHeight()).setFillColor(new Color(255, 255, 250)));
        addLight(new Light("light-area").setLightType(LightType.AREA).setIntensity(0.3).setPosition(0, 0)
                .setSize(world.getWidth()/2, world.getHeight()).setFillColor(new Color(255, 100, 30)));

        setActiveCamera(new Camera("cam01").setTarget(player).setSize(320, 200));
    }

    @Override
    public void input(App app) {
        Entity player = getEntity("player");
        if (player != null) {
            double step = 0.1;
            if (isKeyPressed(KeyEvent.VK_UP)) {
                player.setVelocity(player.getVelocity().getX(), -step * 5);
            }
            if (isKeyPressed(KeyEvent.VK_DOWN)) {
                player.setVelocity(player.getVelocity().getX(), +step);
            }
            if (isKeyPressed(KeyEvent.VK_LEFT)) {
                player.setVelocity(-step, player.getVelocity().getY());
            }
            if (isKeyPressed(KeyEvent.VK_RIGHT)) {
                player.setVelocity(+step, player.getVelocity().getY());
            }
        }
    }

}
