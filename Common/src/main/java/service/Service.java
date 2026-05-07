package service;

import domain.Angajat;
import domain.Cursa;
import domain.Participant;
import repository.RepoAngajat;
import repository.RepoCursa;
import repository.RepoParticipant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Service {
    private static final Logger logger = LogManager.getLogger(Service.class);

    private final RepoAngajat RepoAngajat;
    private final RepoCursa RepoCursa;
    private final RepoParticipant RepoParticipant;

    private Angajat loggedIn = null;

    public Service(RepoAngajat RepoAngajat, RepoCursa RepoCursa, RepoParticipant RepoParticipant) {
        this.RepoAngajat = RepoAngajat;
        this.RepoCursa = RepoCursa;
        this.RepoParticipant = RepoParticipant;
    }

    public boolean login(String username, String password) {
        logger.info("Login attempt for username: " + username);
        Angajat a = RepoAngajat.findByUsername(username);
        if (a != null && a.getPassword().equals(password)) {
            loggedIn = a;
            logger.info("Login successful for: " + username);
            return true;
        }
        logger.warn("Login failed for: " + username);
        return false;
    }

    public void logout() {
        logger.info("Logout: " + (loggedIn != null ? loggedIn.getUsername() : "nobody"));
        loggedIn = null;
    }

    public Angajat getLoggedIn() {
        return loggedIn;
    }

    //returns all curse with their participant counts
    public List<Cursa> getCurse() {
        logger.info("Getting all curse");
        return RepoCursa.getAll();
    }

    //returns all participants belonging to a given team
    public List<Participant> cautaParticipanti(String echipa) {
        logger.info("Searching participants for echipa: " + echipa);
        return RepoParticipant.findByEchipa(echipa);
    }

    //rgisters a new participant and increments the race count
    public void inscrieParticipant(String nume, String cnp, String echipa, int cursaID) {
        logger.info("Registering participant: " + nume + " in cursaID: " + cursaID);
        Participant p = new Participant(0, nume, cnp, echipa, cursaID);
        RepoParticipant.save(p);
        RepoCursa.incrementParticipanti(cursaID);
        logger.info("Participant registered successfully");
    }

    //Updates the team of an existing participant
    public void modificaEchipa(int participantID, String nouaEchipa) {
        logger.info("Updating echipa for participantID: " + participantID + " to: " + nouaEchipa);
        Participant p = RepoParticipant.findById(participantID);
        if (p == null) {
            logger.warn("Participant not found with id: " + participantID);
            return;
        }
        p.setEchipa(nouaEchipa);
        RepoParticipant.update(participantID, p);
        logger.info("Echipa updated successfully");
    }
}