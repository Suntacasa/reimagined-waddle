import network.MotociclismServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import repository.RepoAngajat;
import repository.RepoCursa;
import repository.RepoParticipant;
import service.MotociclismServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StartServer {
    private static final Logger logger = LogManager.getLogger(StartServer.class);
    private static final int PORT = 5555;

    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream is = StartServer.class.getClassLoader().getResourceAsStream("db.config")) {
            if (is == null) {
                logger.error("Fisierul bd.config nu a fost gasit in classpath!");
                System.out.println("Fisierul bd.config nu a fost gasit in classpath!");
                return;
            }
            props.load(is);
        } catch (IOException e) {
            logger.error("Nu pot incarca bd.config: {}", e.getMessage());
            return;
        }

        // Initialize your specific database repositories
        RepoAngajat angajatRepo = new RepoAngajat(props);
        RepoCursa cursaRepo = new RepoCursa(props);
        RepoParticipant participantRepo = new RepoParticipant(props);

        MotociclismServiceImpl service = new MotociclismServiceImpl(angajatRepo, cursaRepo, participantRepo);

        MotociclismServer server = new MotociclismServer(service, PORT);

        logger.info("Pornire server pe portul {}", PORT);
        server.start();
    }
}