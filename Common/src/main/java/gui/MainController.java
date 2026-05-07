package gui;

import domain.Cursa;
import domain.Participant;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.Service;

import java.util.List;

public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    // Curse table
    @FXML private TableView<Cursa> cursaTable;
    @FXML private TableColumn<Cursa, Integer> colCursaId;
    @FXML private TableColumn<Cursa, String> colCapacitate;
    @FXML private TableColumn<Cursa, Integer> colNrParticipanti;

    // Search
    @FXML private TextField searchEchipaField;
    @FXML private TableView<Participant> participantTable;
    @FXML private TableColumn<Participant, String> colNume;
    @FXML private TableColumn<Participant, String> colCapacitateP;

    // Register
    @FXML private TextField numeField;
    @FXML private TextField cnpField;
    @FXML private TextField echipaField;
    @FXML private ComboBox<Cursa> cursaCombo;

    // Modify echipa
    @FXML private TextField participantIdField;
    @FXML private TextField nouaEchipaField;

    @FXML private Label statusLabel;

    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    public void init() {
        logger.info("Initializing MainController");

        //Top tables
        colCursaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCapacitate.setCellValueFactory(new PropertyValueFactory<>("capacitateMotor"));
        colNrParticipanti.setCellValueFactory(new PropertyValueFactory<>("nrParticipanti"));

        //Participanti table
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));

        colCapacitateP.setCellValueFactory(cellData -> {
            int id = cellData.getValue().getCursaID();
            return cursaTable.getItems().stream()
                    .filter(c -> c.getId() == id)
                    .map(c -> new javafx.beans.property.SimpleStringProperty(c.getCapacitateMotor()))
                    .findFirst()
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
        });

        loadCurse();
    }

    private void loadCurse() {
        logger.info("Loading curse");
        List<Cursa> curse = service.getCurse();
        cursaTable.setItems(FXCollections.observableArrayList(curse));
        cursaCombo.setItems(FXCollections.observableArrayList(curse));
        // Show capacitate in combo
        cursaCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cursa c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCapacitateMotor());
            }
        });
        cursaCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cursa c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCapacitateMotor());
            }
        });
    }

    @FXML
    private void handleSearch() {
        String echipa = searchEchipaField.getText().trim();
        if (echipa.isEmpty()) {
            setStatus("Introduceti numele echipei.", false);
            return;
        }
        logger.info("Searching for echipa: " + echipa);
        List<Participant> participants = service.cautaParticipanti(echipa);
        participantTable.setItems(FXCollections.observableArrayList(participants));
        setStatus("Gasiti " + participants.size() + " participanti.", true);
    }

    @FXML
    private void handleInscriere() {
        String nume = numeField.getText().trim();
        String cnp = cnpField.getText().trim();
        String echipa = echipaField.getText().trim();
        Cursa cursa = cursaCombo.getValue();

        if (nume.isEmpty() || cnp.isEmpty() || echipa.isEmpty() || cursa == null) {
            setStatus("Completati toate campurile pentru inscriere.", false);
            return;
        }

        try {
            service.inscrieParticipant(nume, cnp, echipa, cursa.getId());
            loadCurse(); // refresh table to show updated count
            numeField.clear();
            cnpField.clear();
            echipaField.clear();
            cursaCombo.setValue(null);
            setStatus("Participant inscris cu succes!", true);
        } catch (Exception e) {
            logger.error("Error registering participant", e);
            setStatus("Eroare la inscriere: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleModificaEchipa() {
        String idText = participantIdField.getText().trim();
        String nouaEchipa = nouaEchipaField.getText().trim();

        if (idText.isEmpty() || nouaEchipa.isEmpty()) {
            setStatus("Introduceti ID-ul participantului si noua echipa.", false);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            service.modificaEchipa(id, nouaEchipa);
            participantIdField.clear();
            nouaEchipaField.clear();
            setStatus("Echipa modificata cu succes!", true);
        } catch (NumberFormatException e) {
            setStatus("ID invalid.", false);
        } catch (Exception e) {
            logger.error("Error updating echipa", e);
            setStatus("Eroare: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleLogout() {
        logger.info("Logging out");
        service.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load(), 350, 300);
            LoginController controller = loader.getController();
            controller.setService(service);

            Stage stage = new Stage();
            stage.setTitle("Racing - Login");
            stage.setScene(scene);
            stage.show();

            Stage mainStage = (Stage) statusLabel.getScene().getWindow();
            mainStage.close();
        } catch (Exception e) {
            logger.error("Error opening login window", e);
        }
    }

    private void setStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}