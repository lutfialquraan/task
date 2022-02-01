package server;

import utils.Utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

            long size = socketDataInputStream.readLong();


            File uploadedFile = new File(Constant.FOLDER_NAME + FileSystems.getDefault().getSeparator() + fileName);
            writeFile(socketDataInputStream, uploadedFile, size);
            String md5 = socketDataInputStream.readUTF();
            System.out.println("Client MD5 = " + md5);
            System.out.println("Your " + fileName + " have been downloaded successfully on server ...");
            long calculatedCrc = Utilities.calculateCrc(uploadedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile(DataInputStream socketBufferedInputStream,
                           File uploadedFile, long size) throws IOException, NoSuchAlgorithmException {
        if (!uploadedFile.getParentFile().exists()) {
            uploadedFile.getParentFile().mkdirs();
        }
        int currentSize = 0;
        try (FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[Constant.BUFFER_SIZE];
            while (currentSize < size) {
                currentSize += socketBufferedInputStream.read(buffer, 0, buffer.length);
                md.update(buffer, 0, buffer.length);
                bufferedOutputStream.write(buffer, 0, buffer.length);
            }
            //Hash value array
            byte[] digest = md.digest();
            //1 indicates that this is an unsigned integer
            BigInteger bigInteger = new BigInteger(1, digest);
            //Output in hexadecimal form
            System.out.println(bigInteger.toString(16));
        }
    }

}