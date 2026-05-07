package service;

import domain.Angajat;
import domain.Cursa;
import domain.Participant;
import repository.RepoAngajat;
import repository.RepoParticipant;
import repository.RepoCursa;
import utils.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MotociclismServiceImpl implements IMotociclismService {
    private final RepoAngajat angajatRepo;
    private final RepoCursa cursaRepo;
    private final RepoParticipant participantRepo;

    // Thread-safe list of connected clients
    private final List<IMotociclismObserver> observers = new CopyOnWriteArrayList<>();

    public MotociclismServiceImpl(RepoAngajat AngajatRepo, RepoCursa cursaRepo, RepoParticipant participantRepo) {
        this.angajatRepo = AngajatRepo;
        this.cursaRepo = cursaRepo;
        this.participantRepo = participantRepo;
    }

    @Override
    public void addObserver(IMotociclismObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IMotociclismObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(List<Cursa> curse) {
        for (IMotociclismObserver obs : observers) {
            try {
                obs.curseActualizate(curse);
            } catch (MotociclismException e) {
                observers.remove(obs);
            }
        }
    }

    @Override
    public Angajat login(String username, String password) throws MotociclismException {
        //String hashed = PasswordUtil.hashPassword(password);
        //System.out.println("INCERC SA LOGHEZ: username=[" + username + "] parola=[" + password + "]");
        //System.out.println("TOTI ANGAJATII DIN BAZA DE DATE: " + angajatRepo.getAll());
        Angajat Angajat = angajatRepo.findByUsernameAndPassword(username, password);
        if (Angajat == null)
            throw new MotociclismException("Username sau parola incorecte.");
        return Angajat;
    }

    @Override
    public void logout(Angajat Angajat) throws MotociclismException {
    }

    @Override
    public List<Cursa> getAllCurse() throws MotociclismException {
        return cursaRepo.getAll();
    }

    @Override
    public List<Participant> cautaParticipanti(String echipa) throws MotociclismException {
        return participantRepo.findByEchipa(echipa);
    }

    @Override
    public void inscriereParticipant(Cursa cursa, String nume, String cnp, String echipa) throws MotociclismException {
        Connection con = null;
        try {
            con = participantRepo.getConnection();
            con.setAutoCommit(false);

            Participant participant = new Participant(0, nume, cnp, echipa, cursa.getId());
            participantRepo.save(participant, con);

            // Increment the participant count for the race
            cursa.setNrParticipanti(cursa.getNrParticipanti() + 1);
            cursaRepo.update(cursa.getId(), cursa, con);

            con.commit();

            // Broadcast the updated races to all logged-in offices
            notifyObservers(cursaRepo.getAll());

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { /* ignored */ }
            }
            throw new MotociclismException("Eroare la inscriere: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) { /* ignored */ }
            }
        }
    }

    @Override
    public void modificaEchipa(Participant participant, String echipaNoua) throws MotociclismException {
        try {
            participant.setEchipa(echipaNoua);
            participantRepo.update(participant.getId(), participant);

        } catch (Exception e) {
            throw new MotociclismException("Eroare la modificarea echipei: " + e.getMessage());
        }
    }
}