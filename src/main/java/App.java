import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import java.util.Properties;


public class App {
    public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    public static Properties config = new Properties();

    private JFrame window;
    private int debug = 0;
    private boolean exit = false;
    private boolean pause = false;

    public App() {
        System.out.printf(
                "Welcome to %s (%s) !%n", messages.getString("app.name"),
                messages.getString("app.version"));
    }

    public void initConfig(String configFilePath) {
        try {
            config.load(this.getClass().getResourceAsStream(configFilePath));
        } catch (Exception e) {
            System.err.printf("Unable to read configuration file %s:%s",
                    configFilePath,
                    e.getMessage());
        }
    }

    public void run(String[] args) {
        initConfig("config.properties");
        parseArgs(args);
        extractConfigValues();
        prepareWindow();
        System.out.println("RUN !");
        loop();
        dispose();
    }

    private void prepareWindow() {
        window = new JFrame(messages.getString("app.name"));
        window.setPreferredSize(new Dimension(640, 400));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit = true;
                System.exit(0);
            }
        });
        window.pack();
        window.setBackground(Color.BLACK);
        window.setVisible(true);
    }

    public void parseArgs(String[] args) {
        System.out.println("Parse command line arguments...");
        for (String arg : args) {
            String[] keyVal = arg.split("=");
            config.setProperty(keyVal[0], keyVal[1]);
            System.out.printf(" |_ Override config:%s=%s%n", keyVal[0], keyVal[1]);
        }
    }

    private void extractConfigValues() {
        System.out.println("Read configuration...");
        for (String key : config.stringPropertyNames()) {
            switch (key) {
                case "app.debug", "debug", "d" -> {
                    debug = Integer.parseInt(config.getProperty(key));
                    System.out.printf("=> debug level set to %d%n", debug);
                }
                default -> {
                }
            }
        }
    }

    private void loop() {
        do {
            if (!pause) {
                update();
                render();
            }
        } while (!exit);
    }

    private void update() {
    }

    private void render() {
        Graphics2D g = (Graphics2D)window.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,window.getWidth(),window.getHeight());

        g.dispose();
    }


    private void dispose() {
        if (window != null) {
            window.dispose();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.run(args);
    }
}
