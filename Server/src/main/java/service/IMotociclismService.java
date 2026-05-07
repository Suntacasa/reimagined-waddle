package service;

import domain.Angajat;
import domain.Cursa;
import domain.Participant;

import java.util.List;

public interface IMotociclismService {
    Angajat login(String username, String password) throws MotociclismException;
    void logout(Angajat Angajat) throws MotociclismException;

    List<Cursa> getAllCurse() throws MotociclismException;

    List<Participant> cautaParticipanti(String echipa) throws MotociclismException;

    void inscriereParticipant(Cursa cursa, String nume, String cnp, String echipa) throws MotociclismException;

    void modificaEchipa(Participant participant, String echipaNoua) throws MotociclismException;

    void addObserver(IMotociclismObserver observer);
    void removeObserver(IMotociclismObserver observer);
}