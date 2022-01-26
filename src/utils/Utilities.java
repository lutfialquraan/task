package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class Utilities {

    public static long calculateCrc(File file) throws IOException, NoSuchAlgorithmException {
        byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }

    public static long bytesToMeg(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }
}
