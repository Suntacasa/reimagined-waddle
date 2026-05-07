package gui;

import domain.Angajat;
import domain.Cursa;
import domain.Participant;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.IMotociclismObserver;
import service.IMotociclismService;
import service.MotociclismException;

import java.util.List;

public class MainController implements IMotociclismObserver {

    // Tabel 1: Curse (capacitate motor + nr participanti)
    @FXML private TableView<Cursa> curseTable;
    @FXML private TableColumn<Cursa, String> colCapacitate;
    @FXML private TableColumn<Cursa, Integer> colNrParticipanti;

    // Cautare + Tabel 2: Participanti din echipa cautata
    @FXML private TextField cautareEchipaField;
    @FXML private TableView<Participant> participantiTable;
    @FXML private TableColumn<Participant, String> colPartNume;
    @FXML private TableColumn<Participant, Integer> colPartCapacitate; // afisam cursaID sau capacitatea
    @FXML private TableColumn<Participant, String> colPartEchipa;

    // Inscriere
    @FXML private TextField numeField;
    @FXML private TextField cnpField;
    @FXML private TextField echipaField;

    // Modificare
    @FXML private TextField echipaNouaField;

    @FXML private Label statusLabel;
    @FXML private Label angajatLabel;

    private IMotociclismService service;
    private Angajat angajat;
    private Stage stage;

    public void setService(IMotociclismService service) {
        this.service = service;
    }

    public void setAngajat(Angajat angajat) {
        this.angajat = angajat;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void loadData() {
        setupCurseTable();
        setupParticipantiTable();
        loadCurse();

        if (angajat != null) {
            angajatLabel.setText("Oficiu curent: " + angajat.getUsername());
        }

        service.addObserver(this);
    }

    @Override
    public void curseActualizate(List<Cursa> curseNoi) throws MotociclismException {
        Platform.runLater(() -> {
            Cursa selectata = curseTable.getSelectionModel().getSelectedItem();
            int selectedId = selectata != null ? selectata.getId() : -1;

            curseTable.setItems(FXCollections.observableArrayList(curseNoi));

            if (selectedId != -1) {
                for (Cursa c : curseNoi) {
                    if (c.getId() == selectedId) {
                        curseTable.getSelectionModel().select(c);
                        break;
                    }
                }
            }
        });
    }

    private void setupCurseTable() {
        colCapacitate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCapacitateMotor()));
        colNrParticipanti.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNrParticipanti()).asObject());
    }

    private void setupParticipantiTable() {
        colPartNume.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNume()));
        colPartCapacitate.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCursaID()).asObject());
        colPartEchipa.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEchipa()));
    }

    private void loadCurse() {
        try {
            List<Cursa> curse = service.getAllCurse();
            curseTable.setItems(FXCollections.observableArrayList(curse));
        } catch (MotociclismException e) {
            setStatus("Eroare la incarcarea curselor: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleCautare() {
        String echipa = cautareEchipaField.getText().trim();
        if (echipa.isEmpty()) {
            setStatus("Introduceti o echipa pentru cautare!", true);
            return;
        }

        try {
            List<Participant> rezultate = service.cautaParticipanti(echipa);
            participantiTable.setItems(FXCollections.observableArrayList(rezultate));
            setStatus("Gasiti " + rezultate.size() + " participanti din echipa " + echipa, false);
        } catch (MotociclismException e) {
            setStatus("Eroare cautare: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleInscriere() {
        Cursa cursaSelectata = curseTable.getSelectionModel().getSelectedItem();
        if (cursaSelectata == null) {
            setStatus("Selectati o cursa din tabel pentru a inscrie participantul!", true);
            return;
        }

        String nume = numeField.getText().trim();
        String cnp = cnpField.getText().trim();
        String echipa = echipaField.getText().trim();

        if (nume.isEmpty() || cnp.isEmpty()) {
            setStatus("Numele si CNP-ul sunt obligatorii!", true);
            return;
        }

        try {
            service.inscriereParticipant(cursaSelectata, nume, cnp, echipa);
            setStatus("Participant inscris cu succes!", false);
            numeField.clear();
            cnpField.clear();
            echipaField.clear();

            // Re-run search if we are currently looking at their team
            String searchTeam = cautareEchipaField.getText().trim();
            if (!searchTeam.isEmpty() && searchTeam.equalsIgnoreCase(echipa)) {
                handleCautare();
            }
        } catch (MotociclismException e) {
            setStatus(e.getMessage(), true);
        }
    }

    @FXML
    public void handleModificareEchipa() {
        Participant selectat = participantiTable.getSelectionModel().getSelectedItem();
        if (selectat == null) {
            setStatus("Selectati un participant din tabelul de cautare!", true);
            return;
        }

        String echipaNoua = echipaNouaField.getText().trim();
        if (echipaNoua.isEmpty()) {
            setStatus("Introduceti noua echipa!", true);
            return;
        }

        try {
            service.modificaEchipa(selectat, echipaNoua);
            setStatus("Echipa modificata cu succes!", false);
            echipaNouaField.clear();
            handleCautare(); // refresh the table
        } catch (MotociclismException e) {
            setStatus(e.getMessage(), true);
        }
    }

    @FXML
    public void handleLogout() {
        try {
            service.removeObserver(this);
            service.logout(angajat);
        } catch (MotociclismException e) {
            logger.error("Eroare logout: {}", e.getMessage());
        }
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setTitle("Login - Concurs Motociclism");
            LoginController loginController = loader.getController();
            loginController.setService(service);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }

    private static final org.apache.logging.log4j.Logger logger =
            org.apache.logging.log4j.LogManager.getLogger(MainController.class);
}