
package client;

import utils.Utilities;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;

/**
 * <h1>
 * class for Tcp client to upload file to the server
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3
 */
public class TcpClient {

    private Socket socket;
    private BufferedOutputStream socketBufferedOutputStream;
    private DataOutputStream socketDataOutputStream;

    public TcpClient(Socket socket) {
        this.socket = socket;
        try {
            this.socketBufferedOutputStream = new BufferedOutputStream(this.socket.getOutputStream());
            this.socketDataOutputStream = new DataOutputStream(socketBufferedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            System.out.println("This socket is uploading a file: " + this.socket.toString());
            // write the file name to the socket
            String name = file.getName();
            socketDataOutputStream.writeUTF(name);
            // write the file size
            long fileSize = file.length();
            socketDataOutputStream.writeLong(fileSize);

            int bytes = 0;
            byte[] buffer = new byte[Constants.BUFFER_SIZE];
            //Hash value array
            MessageDigest md = MessageDigest.getInstance("MD5");
            // calculate the checksum while reading the file
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                md.update(buffer, 0, buffer.length);
                socketDataOutputStream.write(buffer, 0, bytes);
                socketDataOutputStream.flush();
            }
            // write the checksum to the socket
            String checksum = Utilities.md5ToString(md);
            socketDataOutputStream.writeUTF(checksum);
            socketBufferedOutputStream.flush();

            fileInputStream.close();
            System.out.println("The file (" + name + ") has sent to the server with " + file.length() + " byte size");
            System.out.println("The checksum value for the file (" + name + ") = " + checksum);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}