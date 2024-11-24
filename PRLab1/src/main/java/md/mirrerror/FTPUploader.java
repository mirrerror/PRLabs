package md.mirrerror;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {

    private final String server;
    private final int port;
    private final String user;
    private final String pass;

    public FTPUploader(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public void uploadFileToFTP(File file) throws IOException {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
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
