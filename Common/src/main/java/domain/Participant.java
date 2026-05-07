package domain;

import repository.HasId;

import java.io.Serializable;
import java.util.Objects;

public class Participant implements HasId<Integer>, Serializable {
    private int participantID;
    private String nume;
    private String cnp;
    private String echipa;
    private int cursaID;     //which race they are registered in

    public Participant(int participantID, String nume, String cnp, String echipa, int cursaID) {
        this.participantID = participantID;
        this.nume = nume;
        this.cnp = cnp;
        this.echipa = echipa;
        this.cursaID = cursaID;
    }

    @Override
    public Integer getId() { return participantID; }

    @Override
    public void setId(Integer participantID) { this.participantID = participantID; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }

    public String getEchipa() { return echipa; }
    public void setEchipa(String echipa) { this.echipa = echipa; }

    public int getCursaID() { return cursaID; }
    public void setCursaID(int cursaID) { this.cursaID = cursaID; }

    @Override
    public String toString() {
        return "Participant{participantID=" + participantID +
                ", nume='" + nume + "'" +
                ", cnp='" + cnp + "'" +
                ", echipa='" + echipa + "'" +
                ", cursaID=" + cursaID + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Participant p = (Participant) o;
        return participantID == p.participantID &&
                Objects.equals(cnp, p.cnp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantID, cnp);
    }
}