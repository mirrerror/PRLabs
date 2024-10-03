package md.mirrerror;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        AsosParser asosParser = new AsosParser("https://www.asos.com/men/sale/cat/?cid=8409&ctaref=hp|mw|promo|banner|1|edit|saleupto70offmss");
        List<Product> products = asosParser.parse();

        System.out.println("All the products:\n");
        for(Product product : products) {
            System.out.println(product.getName() + " - " + product.getPriceInGbp() + " GBP - " + product.getPriceInMdl() + " MDL - " + product.getUrl());
        }

        System.out.print("\n\n\n");

        ProductFilter productFilter = new ProductFilter();
        List<FilteredProduct> filteredProducts = productFilter.selectProducts(products, 10, 50);

        System.out.println("Filtered products:\n");
        for(FilteredProduct filteredProduct : filteredProducts) {
            System.out.println(filteredProduct.getName() + " - " + filteredProduct.getPriceInGbp() + " GBP - " + filteredProduct.getPriceInMdl() + " MDL - " + filteredProduct.getUrl() + " - " + filteredProduct.getCreatedAt());
        }

        System.out.print("\n\n\n");

        System.out.println("GET request to asos.com:\n");
        doGetRequest();

        System.out.print("\n\n\n");

        System.out.println("GET request to asos.com using socket:\n");
        doGetRequestUsingSocket();

        System.out.print("\n\n\n");

        System.out.println("Serialization:\n");
        System.out.print(products.get(0).toJson());
        System.out.print("\n\n");
        System.out.print(products.get(0).toXml());
        System.out.print("\n\n");
        System.out.println(filteredProducts.get(0).toJson());
        System.out.print("\n\n");
        System.out.print(filteredProducts.get(0).toXml());

        System.out.print("\n\n\n");

        System.out.println("Custom serialization and deserialization:\n");
        System.out.print(new String(products.get(0).serialize()));
        System.out.print("\n\n");
        System.out.print(Product.deserialize(products.get(0).serialize()));
        System.out.print("\n\n");
        System.out.println(new String(filteredProducts.get(0).serialize()));
        System.out.print("\n\n");
        System.out.print(FilteredProduct.deserialize(filteredProducts.get(0).serialize()));
    }

    private static void doGetRequest() throws IOException {
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
