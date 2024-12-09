package md.mirrerror.prlab3intermediateserver.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class FTPDownloader {

    private final String hostname = "ftp_server";
    private final int port = 21;
    private final String username = "testuser";
    private final String password = "testpass";

    public File downloadFileFromFTP(String remoteFilePath, String localFilePath) throws IOException {
        FTPClient ftpClient = new FTPClient();
        File localFile = new File(localFilePath);

        try {
            ftpClient.connect(hostname, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (OutputStream outputStream = new FileOutputStream(localFile)) {
                boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);

                if (success) {
                    System.out.println("File downloaded successfully: " + localFilePath);
                    System.out.println("File has been saved to: " + localFile.getAbsolutePath());
                } else {
                    System.out.println("Failed to download file: " + localFilePath);
                }
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }

        return localFile;
    }
}
