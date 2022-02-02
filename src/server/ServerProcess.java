package server;

import utils.Utilities;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.security.MessageDigest;

/**
 * <h1>
 * Sever class handle multiple connection from clients
 * and upload multiple files on folder..
 *
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3-.
 */
class ServerProcess {

    public void receive(Socket clientSocket) {
        try {
            File theDir = new File(Constants.FOLDER_NAME);
            if (!theDir.exists()){
                theDir.mkdirs();
            }
            BufferedInputStream socketBufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
            DataInputStream socketDataInputStream = new DataInputStream(socketBufferedInputStream);
            // receive files as the client send them
            while (true) {
                // make sure there is data in the stream
                if (socketDataInputStream.available() != 0) {
                    // read file name from the socket
                    String fileName = socketDataInputStream.readUTF();
                    // read the file size
                    long fileSize = socketDataInputStream.readLong();
                    String uploadedFile = Constants.FOLDER_NAME + FileSystems.getDefault().getSeparator() + fileName;
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    writeFile(socketDataInputStream, uploadedFile, fileSize, md);
                    System.out.println("Your " + fileName + " have been downloaded successfully on server ...");
                    // read checkSum
                    String checksum = socketDataInputStream.readUTF();
                    System.out.println("==================================");
                    System.out.println("Client's CHECKSUM = " + checksum);
                    System.out.println("Server's CHECKSUM = " + Utilities.md5ToString(md));
                    System.out.println("==================================");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile(DataInputStream socketBufferedInputStream,
                           String uploadedFile, long fileSize, MessageDigest md) throws IOException {

        FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
        int bytes = 0;
        byte[] buffer = new byte[Constants.BUFFER_SIZE];
        while (fileSize > 0 && (bytes = socketBufferedInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
            md.update(buffer, 0, buffer.length); // calculate the checksum
            fileOutputStream.write(buffer, 0, bytes);
            fileSize -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }
}