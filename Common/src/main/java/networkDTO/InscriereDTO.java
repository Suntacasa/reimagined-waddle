package networkDTO;

import domain.Cursa;
import java.io.Serializable;

public class InscriereDTO implements Serializable {
    private Cursa cursa;
    private String nume;
    private String cnp;
    private String echipa;

    public InscriereDTO(Cursa cursa, String nume, String cnp, String echipa) {
        this.cursa = cursa;
        this.nume = nume;
        this.cnp = cnp;
        this.echipa = echipa;
    }

    public Cursa getCursa() { return cursa; }
    public String getNume() { return nume; }
    public String getCnp() { return cnp; }
    public String getEchipa() { return echipa; }
}