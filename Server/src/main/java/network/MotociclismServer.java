package network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IMotociclismService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MotociclismServer {
    private static final Logger logger = LogManager.getLogger(MotociclismServer.class);

    private final IMotociclismService service;
    private final int port;
    private volatile boolean running = true;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public MotociclismServer(IMotociclismService service, int port) {
        this.service = service;
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server pornit pe portul {}", port);
            System.out.println("Server pornit pe portul " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client conectat: {}", clientSocket.getInetAddress());

                ClientWorker worker = new ClientWorker(service, clientSocket);
                threadPool.execute(worker);
            }
        } catch (Exception e) {
            logger.error("Eroare server: {}", e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    public void stop() {
        running = false;
        threadPool.shutdown();
    }
}