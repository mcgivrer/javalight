import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.event.*;
import java.util.ResourceBundle;
import java.util.Properties;

public class App implements KeyListener {

	public class static Configuration(){
	
		private Properties props = new Properties();
	
		public Configuration(String configFilePath){
			initDefaultValues();
			load(configFilePath);
		}
		
		private void initDefaultValues(){
			props.setProperty("app.mode",RunningMode.PROD);
			props.setProperty("app.debug","0");
		}
		
		private void load(String filepath){
			try{
				props.load(App.class.getResourceAsStream(filePath);
				readValues();
			}catch(Exception e){
				System.err.println(
					"Unable to read configuration file from %s: %s".formatted(
					filepath, 
					e.getMessages()));
			}
		}
		
		public void mergeArgs(String[] args){
			args.forEach(s->{
				String[] keyVal = s.split("=");
				props.setProperty(keyVame[0],keyVal[1]);
			});
			readValues();
		}
		
		private void readValues(){
			
		}
	}

	public enum RunningMode {
		DEV, TEST, PROD;
	}

	public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
	public static Properties config = new Properties();
	private static final long FPS = 60;
	private JFrame window;

	private int debug = 0;
	private RunningMode mode = RunningMode.PROD;

	private boolean exit = false;
	private boolean pause = false;
	private long time = System.currentTimeMillis();

	public App() {
		System.out.printf("Welcome to %s (%s) !%n", messages.getString("app.name"), messages.getString("app.version"));
	}

	public void initConfig(String configFilePath) {
		try {
			config.load(this.getClass().getResourceAsStream(configFilePath));
		} catch (Exception e) {
			System.err.printf("Unable to read configuration file %s:%s", configFilePath, e.getMessage());
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
		window.addKeyListener(this);
		window.pack();
		window.setBackground(Color.BLACK);
		window.setVisible(true);
		window.createBufferStrategy(3);
		window.requestFocus();
	}

	public void parseArgs(String[] args) {
		System.out.println("Parse command line arguments...");
		for (String arg : args) {
			if (arg.contains("=")) {
				String[] keyVal = arg.split("=");
				config.setProperty(keyVal[0], keyVal[1]);
				System.out.printf(" |_ Override config: %s=%s%n", keyVal[0], keyVal[1]);
			}
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
				case "app.mode", "mode", "m" -> {
					mode = RunningMode.valueOf(config.getProperty(key));
					System.out.printf("=> Running mode set to %s%n", mode);
				}
				default -> {
					System.err.printf("Unnknown argument %s%n", key);
				}
			}
		}
	}

	public <T extends Object> T getConfig(String key, T defaultValue) {
		switch (key) {
		case "app.debug", "debug", "d" -> {
			return (T) Integer.valueOf(config.getProperty(key, "0"));
		}
		case "app.mode", "mode", "m" -> {
			return (T) RunningMode.valueOf(config.getProperty(key, "PROD"));
		}
		default -> {
			System.err.println("Unknown configuration key %s".formatted(key));
		}
		}
		return null;
	}

	private void loop() {
		do {
			if (!pause) {
				update();
				render(time);
			}
			try {
				Thread.sleep(1000 / FPS);
			} catch (Exception e) {
				// something goes wrong in the matrix
			}
		} while (!exit);
	}

	private void update() {
		time += 1000 / FPS;
	}

	private void render(long time) {
		BufferStrategy bs = window.getBufferStrategy();
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, window.getWidth(), window.getHeight());
		g.setColor(Color.WHITE);
		g.drawString("Time is flying %s".formatted(getFormattedTime(time)), (window.getWidth()/2)-60, window.getHeight()/2);
		g.dispose();
		bs.show();
	}

	private String getFormattedTime(long time){
	return "%02d:%02d:%02d.%03d".formatted(
		(time/(1000*3600)) % 24,
		(time/(1000*60)) % 60,
		(time/1000) % 60,
		time % 1000
		);
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

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE -> {
			exit = true;
		}
		case KeyEvent.VK_PAUSE, KeyEvent.VK_P -> {
			pause = !pause;
		}
		default -> {
			// nothing to do in that case
		}
		}
	}

}
