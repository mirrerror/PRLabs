package md.mirrerror;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public FTPUploader(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void uploadFileToFTP(File file) throws IOException {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (FileInputStream inputStream = new FileInputStream(file)) {
                String remoteFilePath = "/" + file.getName();
                boolean success = ftpClient.storeFile(remoteFilePath, inputStream);

                if (success) {
                    System.out.println("File uploaded successfully: " + file.getName());
                } else {
                    System.out.println("Failed to upload file: " + file.getName());
                }
            }
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }
}
