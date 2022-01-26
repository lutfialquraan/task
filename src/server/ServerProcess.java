package server;

import utils.Utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;

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
        try (
                BufferedInputStream socketBufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                DataInputStream socketDataInputStream = new DataInputStream(socketBufferedInputStream)
        ) {

            // read file name from the socket
            String fileName = socketDataInputStream.readUTF();
            // read checkSum
            long checksum = socketDataInputStream.readLong();
            System.out.println("==================================");
            System.out.println("Checksum: " + checksum);
            System.out.println("==================================");


            File uploadedFile = new File(Constant.FOLDER_NAME + FileSystems.getDefault().getSeparator() + fileName);
            writeFile(socketDataInputStream, uploadedFile);

            System.out.println("Your " + fileName + " have been downloaded successfully on server ...");
            long calculatedCrc = Utilities.calculateCrc(uploadedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile(DataInputStream socketBufferedInputStream,
                           File uploadedFile) throws IOException {
        if (!uploadedFile.getParentFile().exists()) {
            uploadedFile.getParentFile().mkdirs();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            byte[] buffer = new byte[Constant.BUFFER_SIZE];
            while (socketBufferedInputStream.read(buffer, 0, buffer.length) != -1) {
                bufferedOutputStream.write(buffer, 0, buffer.length);
            }
        }
    }

}