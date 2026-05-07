package gui;

import domain.Angajat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.IMotociclismService;
import service.MotociclismException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label errorLabel;

    private IMotociclismService service;

    public void setService(IMotociclismService service) {
        this.service = service;
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username si parola sunt obligatorii.");
            return;
        }

        try {
            Angajat angajat = service.login(username, password);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Oficiu inscriere - " + angajat.getUsername());

            MainController mainController = loader.getController();
            mainController.setService(service);
            mainController.setAngajat(angajat);
            mainController.setStage(stage);
            mainController.loadData();

            stage.show();

            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
        } catch (MotociclismException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Eroare la deschiderea ferestrei: " + e.getMessage());
            e.printStackTrace();
        }
    }
}