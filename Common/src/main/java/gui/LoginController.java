package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.Service;

public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        if (service.login(username, password)) {
            logger.info("Login successful, opening main window");
            openMainWindow();
        } else {
            logger.warn("Login failed for: " + username);
            errorLabel.setText("Invalid username or password.");
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            MainController controller = loader.getController();
            controller.setService(service);
            controller.init();

            Stage stage = new Stage();
            stage.setTitle("Racing - " + service.getLoggedIn().getUsername());
            stage.setScene(scene);
            stage.show();

            // Close login window
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            logger.error("Error opening main window", e);
            errorLabel.setText("Error opening main window.");
        }
    }
}