package network;

import domain.Angajat;
import domain.Cursa;
import domain.Participant;
import javafx.application.Platform;
import networkDTO.*;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerProxy implements IMotociclismService {
    private static final Logger logger = LogManager.getLogger(ServerProxy.class);

    private final String host;
    private final int port;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private final BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>();
    private IMotociclismObserver localObserver;
    private volatile boolean connected = false;

    public ServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws MotociclismException {
        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            startResponseReaderThread();
            logger.info("Conectat la server {}:{}", host, port);
        } catch (IOException e) {
            throw new MotociclismException("Nu ma pot conecta la server: " + e.getMessage(), e);
        }
    }

    private void startResponseReaderThread() {
        Thread t = new Thread(() -> {
            while (connected) {
                try {
                    Response response = (Response) input.readObject();
                    logger.info("Raspuns primit: {}", response.getType());

                    if (response.getType() == ResponseType.UPDATE_CURSE) {
                        if (localObserver != null) {
                            List<Cursa> curse = (List<Cursa>) response.getData();
                            Platform.runLater(() -> {
                                try {
                                    localObserver.curseActualizate(curse);
                                } catch (MotociclismException e) {
                                    logger.error("Eroare la actualizarea GUI: {}", e.getMessage());
                                }
                            });
                        }
                    } else {
                        responseQueue.put(response);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.info("Conexiune inchisa: {}", e.getMessage());
                    connected = false;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ResponseReaderThread");
        t.setDaemon(true);
        t.start();
    }

    private Response sendAndReceive(Request request) throws MotociclismException {
        try {
            synchronized (output) {
                output.writeObject(request);
                output.flush();
            }
            return responseQueue.take();
        } catch (IOException | InterruptedException e) {
            throw new MotociclismException("Eroare comunicare cu serverul: " + e.getMessage(), e);
        }
    }

    private void checkResponse(Response response) throws MotociclismException {
        if (response.getType() == ResponseType.ERROR) {
            throw new MotociclismException(response.getErrorMessage());
        }
    }

    @Override
    public Angajat login(String username, String password) throws MotociclismException {
        Angajat dummy = new Angajat(0, username, password);
        Response response = sendAndReceive(new Request(RequestType.LOGIN, dummy));
        checkResponse(response);
        return (Angajat) response.getData();
    }

    @Override
    public void logout(Angajat angajat) throws MotociclismException {
        Response response = sendAndReceive(new Request(RequestType.LOGOUT, angajat));
        checkResponse(response);
    }

    @Override
    public List<Cursa> getAllCurse() throws MotociclismException {
        Response response = sendAndReceive(new Request(RequestType.GET_ALL_CURSE, null));
        checkResponse(response);
        return (List<Cursa>) response.getData();
    }

    @Override
    public List<Participant> cautaParticipanti(String echipa) throws MotociclismException {
        Response response = sendAndReceive(new Request(RequestType.CAUTA_PARTICIPANTI, echipa));
        checkResponse(response);
        return (List<Participant>) response.getData();
    }

    @Override
    public void inscriereParticipant(Cursa cursa, String nume, String cnp, String echipa) throws MotociclismException {
        InscriereDTO dto = new InscriereDTO(cursa, nume, cnp, echipa);
        Response response = sendAndReceive(new Request(RequestType.INSCRIERE_PARTICIPANT, dto));
        checkResponse(response);
    }

    @Override
    public void modificaEchipa(Participant participant, String echipaNoua) throws MotociclismException {
        ModificareEchipaDTO dto = new ModificareEchipaDTO(participant, echipaNoua);
        Response response = sendAndReceive(new Request(RequestType.MODIFICA_ECHIPA, dto));
        checkResponse(response);
    }

    @Override
    public void addObserver(IMotociclismObserver observer) {
        this.localObserver = observer;
    }

    @Override
    public void removeObserver(IMotociclismObserver observer) {
        if (this.localObserver == observer) {
            this.localObserver = null;
        }
    }
}