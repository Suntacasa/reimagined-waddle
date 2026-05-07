package service;

import domain.Cursa;
import java.util.List;

public interface IMotociclismObserver {
    // The server calls this on each connected client when a new participant is registered
    void curseActualizate(List<Cursa> curse) throws MotociclismException;
}