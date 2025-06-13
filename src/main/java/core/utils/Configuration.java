package core.utils;

import static core.App.*;

import java.awt.Dimension;
import java.util.Properties;

import core.App;
import core.App.LogLevel;
import core.App.RunningMode;

public class Configuration {
    Properties attributes = new Properties();
    private App app;

    public Configuration(App app) {
        this.app = app;

    }

    public Configuration load(String configFilePath) {
        try {
            attributes.load(this.getClass().getResourceAsStream(configFilePath));
            extractConfigValues();
        } catch (Exception e) {
            log(App.class, LogLevel.ERROR, "Unable to read configuration file %s: %s", configFilePath, e.getMessage());
        }
        return this;
    }

    public Configuration parseArgs(String[] args) {
        log(App.class, LogLevel.INFO, "Parse command line arguments...");
        for (String arg : args) {
            String[] keyVal = arg.split("=");
            attributes.setProperty(keyVal[0], keyVal[1]);
            log(App.class, LogLevel.INFO, " |_ Override config:%s=%s", keyVal[0], keyVal[1]);
        }
        return this;
    }

    public Configuration extractConfigValues() {
        log(App.class, LogLevel.INFO, "Read configuration...");
        for (String key : attributes.stringPropertyNames()) {
            switch (key) {
            case "app.debug", "debug", "d" -> {
                app.debug = Integer.parseInt(attributes.getProperty(key, "0"));
                log(App.class, LogLevel.INFO, "=> debug level overriden with arg %d", app.debug);
            }
            case "app.mode", "mode", "m" -> {
                String mode = attributes.getProperty(key, "PROD");
                app.mode = RunningMode.valueOf(mode);
                log(App.class, LogLevel.INFO, "=> mode overriden with arg %s", app.mode);
            }
            default -> {
            }
            }

        }
        return this;
    }

    private <T> T getConfig(String key, T defaultValue) {
        switch (key) {
        case "app.debug", "debug", "d" -> {
            return (T) Integer.valueOf(attributes.getProperty(key, "0"));
        }
        case "app.mode", "mode", "m" -> {
            return (T) RunningMode.valueOf(attributes.getProperty(key, "PROD"));
        }
        case "app.window.size", "ws" -> {
            String[] size = attributes.getProperty(key, "720x460").split("x");
            return (T) new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
        }
        case "app.gfx.rendering.buffer.size", "rbs" -> {
            String[] size = attributes.getProperty(key, "320x200").split("x");
            return (T) new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
        }
        default -> log(App.class, LogLevel.WARN, "Unknown configuration key %s", key);
        }
        return null;
    }

    public <T> T get(String key, T defaultValue) {
        return (T) getConfig(key, defaultValue);
    }
}
