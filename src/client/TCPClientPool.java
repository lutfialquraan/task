package client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>
 * A this class is to manage a pool of cached TCP clients can be used again in the future to increase the overall throughput *
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3
 */

public class TCPClientPool {
    private BlockingQueue<TcpClient> clientConnectionPool;
    private AtomicInteger currentNumOfConnections;
    private int maxNumOfConnections;

    public TCPClientPool(int numberOfConnections) {
        this.currentNumOfConnections = new AtomicInteger(0);
        this.maxNumOfConnections = numberOfConnections;
        this.clientConnectionPool = new ArrayBlockingQueue<>(numberOfConnections);
    }

    public TcpClient getClient() {
        try {
            if (currentNumOfConnections.get() < maxNumOfConnections && clientConnectionPool.isEmpty()) {
                currentNumOfConnections.incrementAndGet();
                Socket socket = new Socket(Constants.IP_ADDRESS, Constants.SERVER_PORT);
                return new TcpClient(socket);
            }
            return clientConnectionPool.take();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addConnection(TcpClient tcpClient) {
        clientConnectionPool.add(tcpClient);
    }

    public void closeConnections() {
        for (TcpClient client : clientConnectionPool) {
            try {
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}