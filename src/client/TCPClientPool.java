package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

    public TCPClientPool(int numberOfConnections) {
        fillConnectionPool(numberOfConnections);
    }

    private void fillConnectionPool(int numberOfConnections) {
        this.clientConnectionPool = new ArrayBlockingQueue<>(numberOfConnections);
        for (int i = 0; i < numberOfConnections; ++i) {
            this.clientConnectionPool.add(new TcpClient());
        }
    }

    public TcpClient getClient() {
        try {
            return clientConnectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addConnection(TcpClient tcpClient) {
        clientConnectionPool.add(tcpClient);
    }

}