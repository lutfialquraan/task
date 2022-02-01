
package client;

import utils.Utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * <h1>
 * class for Tcp client to upload file to the server
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3
 */
public class TcpClient {


    public void upload(File file) {
        try (
                Socket clientSocket = new Socket(Constants.IP_ADDRESS, Constants.SERVER_PORT);
                BufferedOutputStream socketBufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                DataOutputStream socketDataOutputStream = new DataOutputStream(socketBufferedOutputStream);
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            clientSocket.setKeepAlive(true);

            // write the file name to the socket
            String name = file.getName();
            socketDataOutputStream.writeUTF(name);
            socketDataOutputStream.flush();

            // calculate the checksum
            long checksum = Utilities.calculateCrc(file);
            // write the checksum to the socket
            socketDataOutputStream.writeLong(checksum);
            socketDataOutputStream.flush();
            System.out.println("The checksum value for the file (" + name + ") = " + checksum);

            // write the file to the socket
            byte[] buffer = new byte[Constants.BUFFER_SIZE];
            while (bufferedInputStream.read(buffer, 0, buffer.length) != -1) {
                socketDataOutputStream.write(buffer, 0, buffer.length);
            }
            socketDataOutputStream.flush();
            System.out.println("The file (" + name + ") has sent to the server with " + file.length() + " byte size");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}