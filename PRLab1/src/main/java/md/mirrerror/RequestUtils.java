package md.mirrerror;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestUtils {

    public static void doGetRequest() throws IOException {
        URL url = new URL("https://www.asos.com");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            System.out.println(content);
        } else {
            System.out.println("GET request failed. Response Code: " + responseCode);
        }
    }

    public static void doGetRequestUsingSocket() throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        SSLSocket socket = (SSLSocket) factory.createSocket("www.asos.com", 443);

        socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

        OutputStream outputStream = socket.getOutputStream();

        String httpRequest = "GET / HTTP/1.1\r\n" +
                "Host: www.asos.com\r\n" +
                "User-Agent: Mozilla/5.0\r\n" +
                "Connection: close\r\n\r\n";

        outputStream.write(httpRequest.getBytes());
        outputStream.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n");
        }

        in.close();
        outputStream.close();
        socket.close();

        System.out.println(response);
    }

}
