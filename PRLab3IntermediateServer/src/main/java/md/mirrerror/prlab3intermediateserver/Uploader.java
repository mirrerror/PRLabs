package md.mirrerror.prlab3intermediateserver;

import lombok.RequiredArgsConstructor;
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

    @Async
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void fetchAndSendFile() throws IOException {
        System.out.println("Fetching file from FTP server...");

        File downloadedFile = ftpDownloader.downloadFileFromFTP("/remote/path/to/file.txt", "local/path/to/file.txt");

        if (downloadedFile.exists()) {
            FileUtils.uploadFileToServer(downloadedFile);
        }
    }

}