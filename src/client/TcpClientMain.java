package client;

import utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>
 * Tcp Client Application to transfer multiple files over separate connections
 * </h1>
 *
 * @author Lutfi Omar
 * @version 1.3
 */

class TcpClientMain {

    public static void main(String[] args) throws Exception {

        // read the folder from the argument
        String folder = args[0];
        // read the concurrency level
        int concurrencyLevel = Integer.parseInt(args[1]);
        // get list of Files
        File[] files = new File(folder).listFiles();
        //  list of the files sizes
        List<AtomicLong> fileSizes = new ArrayList<>();
        // create CLIENT POOL according to the concurrency level
        TCPClientPool clientPool = new TCPClientPool(concurrencyLevel);
        // get instance from the file-upload fileUploadFacade
        FileUploadFacade fileUploadFacade = FileUploadFacade.getInstance(clientPool);
        // create fixed pool according to the concurrency level
        ExecutorService executorService = Executors.newFixedThreadPool(concurrencyLevel);

        // store the time before starting sending the files
        long startTime = System.currentTimeMillis();
        sendFiles(files, executorService, fileSizes, fileUploadFacade);

        executorService.shutdown();
        // await for threads Termination before executing the next lines
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        // store the time when the transferring finishes
        long endTime = System.currentTimeMillis();

        displayThroughput(fileSizes, startTime, endTime, concurrencyLevel);
    }

    private static void sendFiles(File[] files, ExecutorService executorService, List<AtomicLong> fileSizes, FileUploadFacade fileUploadFacade) {
        for (File file : files) {
            executorService.submit(() -> {
                AtomicLong atomicLong = new AtomicLong(0);
                fileUploadFacade.upload(file, atomicLong);
                fileSizes.add(atomicLong);
            });
        }
    }

    private static void displayThroughput(List<AtomicLong> fileSizes, long startTime, long endTime, int concurrencyLevel) {
        long totalBytes = fileSizes.stream().map(AtomicLong::get).reduce(0L, Long::sum);

        long totalTimeInMills = endTime - startTime;
        System.out.println(startTime);
        System.out.println("=====================================");
        System.out.println("finish in " + totalTimeInMills + " milli seconds");
        System.out.println("=====================================");
        long totalInMegaBytes = Utilities.bytesToMeg(totalBytes);
        System.out.println("Overall data size = " + totalInMegaBytes + " MB");
        System.out.println("=====================================");
        long totalInMegaBits = totalInMegaBytes * 8;
        System.out.println("Throughout over " + concurrencyLevel + " concurrency level = " + (totalInMegaBits) / totalTimeInMills + " Mbpms");
    }
}