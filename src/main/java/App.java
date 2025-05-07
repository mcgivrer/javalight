public class App{
  public App(){
    System.out.println("Welcome to JDemo !");    
  }
  public static void main(String[] args){
    App app = new App();
    app.run(args);
  }
  public void run(String[] args){
    System.out.println("Hello World!");
  }
}
