public class Main {

    public static void main(String[] args){
        System.out.println("App started");
        ServerController serverController = new ServerController(1992);
        serverController.listenForConnection();
    }

}
