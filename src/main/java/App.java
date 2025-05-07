public class App{
	public static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages"); 
  public App(){
    System.out.println("Welcome to %s (%s) !".formatted(messages.getString("app.name"),messages.getString("app.version"));    
  }
  public static void main(String[] args){
    App app = new App();
    app.run(args);
  }
  public void run(String[] args){
    System.out.println("RUN !");
  }
}
