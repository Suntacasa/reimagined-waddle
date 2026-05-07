package domain;

import repository.HasId;

import java.io.Serializable;
import java.util.Objects;

public class Cursa implements HasId<Integer>, Serializable {
    private int cursaID;
    private String capacitateMotor;  // e.g. "125mc", "250mc"
    private int nrParticipanti;

    public Cursa(int cursaID, String capacitateMotor, int nrParticipanti) {
        this.cursaID = cursaID;
        this.capacitateMotor = capacitateMotor;
        this.nrParticipanti = nrParticipanti;
    }

    @Override
    public Integer getId() { return cursaID; }

    @Override
    public void setId(Integer cursaID) { this.cursaID = cursaID; }

    public String getCapacitateMotor() { return capacitateMotor; }
    public void setCapacitateMotor(String capacitateMotor) { this.capacitateMotor = capacitateMotor; }

    public int getNrParticipanti() { return nrParticipanti; }
    public void setNrParticipanti(int nrParticipanti) { this.nrParticipanti = nrParticipanti; }

    @Override
    public String toString() {
        return "Cursa{cursaID=" + cursaID +
                ", capacitateMotor='" + capacitateMotor + "'" +
                ", nrParticipanti=" + nrParticipanti + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cursa cursa = (Cursa) o;
        return cursaID == cursa.cursaID &&
                Objects.equals(capacitateMotor, cursa.capacitateMotor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursaID, capacitateMotor);
    }
}