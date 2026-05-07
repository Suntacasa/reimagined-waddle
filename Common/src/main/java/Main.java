import domain.Angajat;
import domain.Cursa;
import domain.Participant;
import gui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.RepoAngajat;
import repository.RepoCursa;
import repository.RepoParticipant;
import service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/*public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Load db config
        Properties props = new Properties();
        try {
            props.load(new FileReader("C:\\Users\\User\\IdeaProjects\\mpp1\\src\\main\\java\\db.config"));
            logger.info("Loaded db.config");
        } catch (IOException e) {
            logger.error("Could not load db.config: {}", e.getMessage());
            return;
        }

        // Init repos
        RepoAngajat angajatRepo = new RepoAngajat(props);
        RepoCursa cursaRepo = new RepoCursa(props);
        RepoParticipant participantRepo = new RepoParticipant(props);

        logger.info("Testing Angajat...");
        angajatRepo.save(new Angajat(0, "admin", "pass123"));
        Angajat found = angajatRepo.findByUsername("admin");
        logger.info("Found angajat: {}", found);

        logger.info("Testing Cursa...");
        cursaRepo.save(new Cursa(0, "125mc", 0));
        cursaRepo.save(new Cursa(0, "250mc", 0));
        List<Cursa> curse = cursaRepo.getAll();
        logger.info("All curse: {}", curse);

        logger.info("Testing Participant...");
        int cursaID = curse.get(0).getId();
        participantRepo.save(new Participant(0, "Ion Popescu", "1234567890123", "Suzuki", cursaID));
        List<Participant> participants = participantRepo.findByEchipa("Suzuki");
        logger.info("Suzuki participants: {}", participants);

        logger.info("DONE");
    }
}*/

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Application starting");

        // Load config
        Properties props = new Properties();
        var resourceStream = Main.class.getClassLoader().getResourceAsStream("db.config");
        if (resourceStream == null) {
            throw new RuntimeException("Could not find db.config in resources folder!");
        }
        props.load(resourceStream);

        // Wire up repos and service
        RepoAngajat RepoAngajat = new RepoAngajat(props);
        RepoCursa RepoCursa = new RepoCursa(props);
        RepoParticipant RepoParticipant = new RepoParticipant(props);
        Service service = new Service(RepoAngajat, RepoCursa, RepoParticipant);

        // Open login window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load(), 350, 300);
        LoginController controller = loader.getController();
        controller.setService(service);

        primaryStage.setTitle("Racing - Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.info("Login window opened");
    }

    public static void main(String[] args) {
        launch(args);
    }
}