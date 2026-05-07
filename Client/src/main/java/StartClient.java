import gui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.ServerProxy;
import service.MotociclismException;

public class StartClient extends Application {
    private static final String HOST = "localhost";
    private static final int PORT = 5555;

    private ServerProxy proxy;

    @Override
    public void start(Stage primaryStage) throws Exception {
        proxy = new ServerProxy(HOST, PORT);
        try {
            proxy.connect();
        } catch (MotociclismException e) {
            System.err.println("Nu ma pot conecta la server: " + e.getMessage());
            System.exit(1);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Login - Concurs Motociclism");

        LoginController loginController = loader.getController();
        loginController.setService(proxy); // proxy-ul e IMotociclismService

        primaryStage.setOnCloseRequest(e -> {
            try { proxy.logout(null); } catch (Exception ignored) {}
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}