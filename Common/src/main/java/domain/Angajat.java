package domain;

import repository.HasId;

import java.io.Serializable;
import java.util.Objects;

public class Angajat implements HasId<Integer>, Serializable {
    private int angajatID;
    private String username;
    private String password;

    public Angajat(int angajatID, String username, String password) {
        this.angajatID = angajatID;
        this.username = username;
        this.password = password;
    }

    @Override
    public Integer getId() {
        return angajatID;
    }

    @Override
    public void setId(Integer angajatID) {
        this.angajatID = angajatID;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Angajat{angajatID=" + angajatID + ", username='" + username + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Angajat angajat = (Angajat) o;
        return angajatID == angajat.angajatID &&
                Objects.equals(username, angajat.username) &&
                Objects.equals(password, angajat.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(angajatID, username, password);
    }
}