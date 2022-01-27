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
        argumentValidator(args);
        // read folder path
        String folder = args[0];
        // read concurrency Level
        int concurrencyLevel = args[1] != null ? Integer.parseInt(args[1]) : 1;

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
        long startTime = System.nanoTime();

        // send the files
        sendFiles(files, executorService, fileSizes, fileUploadFacade);
        executorService.shutdown();
        // await for threads Termination before executing the next lines
        executorService.awaitTermination(60, TimeUnit.SECONDS);
        // store the time when the transferring finishes
        long endTime = System.nanoTime();

        displayThroughput(fileSizes, startTime, endTime, concurrencyLevel);
    }

    private static void argumentValidator(String[] args) {
        int argumentLength = args.length;
        if (argumentLength == 0) {
            System.out.println("Proper Usage is: java TcpClientMain FOLDER_PATH ConcurrencyLevel");
            System.exit(0);
        }

        if (argumentLength >= 1) {
            try {
                if (!new File(args[0]).isDirectory()) {
                    throw new IllegalAccessException();
                }
            } catch (Exception e) {
                System.out.println("This is not a Folder: Please make sure your folder path is correct!");
                System.exit(0);
            }
        }
        if (args.length == 2) {
            try {
                Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("This is not a number: Please make sure to enter an integer number!");
                System.exit(0);
            }
        }
    }

    private static void sendFiles(File[] files, ExecutorService executorService, List<AtomicLong> fileSizes, FileUploadFacade fileUploadFacade) {
        for (File file : files) {
            executorService.submit(() -> {
                AtomicLong fileSize = new AtomicLong(0);
                fileUploadFacade.upload(file, fileSize);
                fileSizes.add(fileSize);
            });
        }
    }

    private static void displayThroughput(List<AtomicLong> fileSizes, long startTime, long endTime, int concurrencyLevel) {
        long totalBytes = fileSizes.stream().map(AtomicLong::get).reduce(0L, Long::sum);
        long totalTimeInNano = endTime - startTime;
        double totalTimeInSeconds = (double) totalTimeInNano / 1000000000;
        System.out.println("=====================================");
        System.out.println("finish in " + totalTimeInSeconds + " seconds");
        System.out.println("=====================================");
        long totalInMegaBytes = Utilities.bytesToMeg(totalBytes);
        System.out.println("Overall data size = " + totalInMegaBytes + " MB");
        System.out.println("=====================================");
        long totalInMegaBits = totalInMegaBytes * 8;
        System.out.println("Throughout over " + concurrencyLevel + " concurrency level = " + (totalInMegaBits) / totalTimeInSeconds + " Mbps");
    }
}