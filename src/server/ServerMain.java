package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * <h1>
 * Server application to receive request from multiple client and
 * upload file on server on specific folder.
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3
 */
public class ServerMain {
    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            // create instance from ServerProcess
            ServerSocket serverSocket = new ServerSocket(Constant.SERVER_PORT);
            System.out.printf("- Server is listening on port : %d\n", Constant.SERVER_PORT);

            // create worker for all tasks
            ServerProcess worker = new ServerProcess();

            while (true) {
                // accept connection from client and initialize server process
                Socket clientSocket = serverSocket.accept();
                clientSocket.setKeepAlive(true);
                // run the method on ServerProcess
                executorService.submit(() -> worker.receive(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}