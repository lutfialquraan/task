package utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public final class Utilities {

    public static String md5ToString(MessageDigest md){
        byte[] digest = md.digest();
        //1 indicates that this is an unsigned integer
        BigInteger bigInteger = new BigInteger(1, digest);
        //Output in hexadecimal form
        return bigInteger.toString(16);

    }
    public static long bytesToMeg(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
}
}
