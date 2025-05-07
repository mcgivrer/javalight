import java.util.ResourceBundle;
import java.util.Properties;


public class App{
	public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages"); 
	public static Properties config = new Properties();
	private int debug=0;
	public App(){
		System.out.println(
		"Welcome to %s (%s) !"
		.formatted(
			messages.getString("app.name"),
			messages.getString("app.version")));
	}
	
	public void initConfig(String configFilePath){
		try{
			config.load(this.getClass().getResourceAsStream(configFilePath));
		}catch(Exception e){
			System.err.printf("Unable to reqd configruation file %s:%s",
				configFilePath,
				e.getMessage());
		}
	}
	
	public void run(String[] args){
		initConfig("config.properties");
		parseArgs(args);
		extractConfigValues();
		System.out.println("RUN !");
	}
	
	public void parseArgs(String[] args){
		System.out.println("Parse command line arguments...");
		for(String arg:args){
			String[] keyVal=arg.split("=");
			config.setProperty(keyVal[0],keyVal[1]);
			System.out.printf(" |_ Override config:%s=%s%n",keyVal[0],keyVal[1]);
		}
	}
	
	private void extractConfigValues(){
		System.out.println("Read configuration...");
		for(String key:config.stringPropertyNames()){
			switch(key){
				case "app.debug","debug","d" ->{
					debug = Integer.parseInt(config.getProperty(key));
					System.out.printf("=> debug level set to %d%n",debug);
				}
				default ->{}
			}
		}
	}

	public static void main(String[] args){
		App app = new App();
		app.run(args);
	}
}
