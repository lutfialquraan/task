package client;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>
 * This single-ton facade is an object that serves as a front-facing interface to hide sending file complexity
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3
 */
public class FileUploadFacade {
    private static final Object LOCK = new Object();
    private static volatile FileUploadFacade instance;
    private TCPClientPool clientPool;

    private FileUploadFacade(TCPClientPool clientPool) {
        this.clientPool = clientPool;
    }

    public static FileUploadFacade getInstance(TCPClientPool clientPool) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new FileUploadFacade(clientPool);
                }
            }
        }
        return instance;
    }

    public void upload(File file, AtomicLong bytesSent) {
        TcpClient client = clientPool.getClient();
        client.upload(file, bytesSent);
        clientPool.addConnection(client);
    }
}