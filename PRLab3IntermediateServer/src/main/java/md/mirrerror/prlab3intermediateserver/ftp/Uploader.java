package md.mirrerror.prlab3intermediateserver.ftp;

import lombok.RequiredArgsConstructor;
import md.mirrerror.prlab3intermediateserver.FileManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class Uploader {

    private final FTPDownloader ftpDownloader;
    private final FileManager fileManager;

    @Async
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void fetchAndSendFile() throws IOException {
        System.out.println("Fetching file from FTP server...");

        File downloadedFile = ftpDownloader.downloadFileFromFTP("/products.json", "downloaded_products.json");

        if (downloadedFile.exists()) {
            fileManager.uploadFileToServer(downloadedFile);
        }
    }

}
