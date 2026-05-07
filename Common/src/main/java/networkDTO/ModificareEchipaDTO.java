package networkDTO;

import domain.Participant;
import java.io.Serializable;

public class ModificareEchipaDTO implements Serializable {
    private Participant participant;
    private String echipaNoua;

    public ModificareEchipaDTO(Participant participant, String echipaNoua) {
        this.participant = participant;
        this.echipaNoua = echipaNoua;
    }

    public Participant getParticipant() { return participant; }
    public String getEchipaNoua() { return echipaNoua; }
}