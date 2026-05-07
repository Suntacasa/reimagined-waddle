package network;

import domain.Angajat;
import domain.Cursa;
import domain.Participant;
import networkDTO.InscriereDTO;
import networkDTO.ModificareEchipaDTO;
import networkDTO.Request;
import networkDTO.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IMotociclismObserver;
import service.IMotociclismService;
import service.MotociclismException;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientWorker implements Runnable, IMotociclismObserver {
    private static final Logger logger = LogManager.getLogger(ClientWorker.class);

    private final IMotociclismService service;
    private final Socket clientSocket;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected = true;

    public ClientWorker(IMotociclismService service, Socket clientSocket) {
        this.service = service;
        this.clientSocket = clientSocket;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());
            logger.info("ClientWorker creat pentru: {}", clientSocket.getInetAddress());
        } catch (IOException e) {
            logger.error("Eroare la crearea stream-urilor: {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Request request = (Request) input.readObject();
                logger.info("Request primit: {}", request.getType());
                Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.info("Client deconectat: {}", e.getMessage());
                connected = false;
            }
        }
        service.removeObserver(this);
        try { clientSocket.close(); } catch (IOException ignored) {}
        logger.info("ClientWorker oprit");
    }

    private Response handleRequest(Request request) {
        try {
            switch (request.getType()) {
                case LOGIN -> {
                    Angajat Angajat = (Angajat) request.getData();
                    Angajat logat = service.login(Angajat.getUsername(), Angajat.getPassword());
                    service.addObserver(this);
                    return Response.ok(logat);
                }
                case LOGOUT -> {
                    Angajat Angajat = (Angajat) request.getData();
                    service.logout(Angajat);
                    service.removeObserver(this);
                    connected = false;
                    return Response.ok(null);
                }
                case GET_ALL_CURSE -> {
                    List<Cursa> curse = service.getAllCurse();
                    return Response.ok(curse);
                }
                case CAUTA_PARTICIPANTI -> {
                    String echipa = (String) request.getData(); // Or use a CautareEchipaDTO
                    List<Participant> rezultate = service.cautaParticipanti(echipa);
                    return Response.ok(rezultate);
                }
                case INSCRIERE_PARTICIPANT -> {
                    InscriereDTO dto = (InscriereDTO) request.getData();
                    service.inscriereParticipant(dto.getCursa(), dto.getNume(), dto.getCnp(), dto.getEchipa());
                    return Response.ok(null);
                }
                case MODIFICA_ECHIPA -> {
                    ModificareEchipaDTO dto = (ModificareEchipaDTO) request.getData();
                    service.modificaEchipa(dto.getParticipant(), dto.getEchipaNoua());
                    return Response.ok(null);
                }
                default -> {
                    return Response.error("Request tip necunoscut: " + request.getType());
                }
            }
        } catch (MotociclismException e) {
            logger.error("Eroare la procesarea requestului: {}", e.getMessage());
            return Response.error(e.getMessage());
        }
    }

    @Override
    public void curseActualizate(List<Cursa> curse) throws MotociclismException {
        try {
            sendResponse(Response.update(curse)); // Must match your UPDATE response type mapping
        } catch (Exception e) {
            logger.error("Eroare la trimiterea notificarii: {}", e.getMessage());
            throw new MotociclismException("Eroare notificare client", e);
        }
    }

    private synchronized void sendResponse(Response response) throws IOException {
        output.writeObject(response);
        output.flush();
    }
}