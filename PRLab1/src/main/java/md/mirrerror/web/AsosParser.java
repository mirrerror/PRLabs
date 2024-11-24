package md.mirrerror.web;

import md.mirrerror.FTPUploader;
import md.mirrerror.RabbitMQPublisher;
import md.mirrerror.models.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsosParser {

    private String protocol;
    private String hostname;
    private String urlPath;

    private String rabbitMQHost;
    private String rabbitMQUsername;
    private String rabbitMQPassword;

    private String ftpHost;
    private String ftpUsername;
    private String ftpPassword;
    private int ftpPort;

    public AsosParser(String protocol, String hostname, String urlPath, String rabbitMQHost, String rabbitMQUsername, String rabbitMQPassword) {
        this.protocol = protocol;
        this.hostname = hostname;
        this.urlPath = urlPath;
        this.rabbitMQHost = rabbitMQHost;
        this.rabbitMQUsername = rabbitMQUsername;
        this.rabbitMQPassword = rabbitMQPassword;
    }

    public AsosParser(String protocol, String hostname, String urlPath) {
        this.protocol = protocol;
        this.hostname = hostname;
        this.urlPath = urlPath;
    }

    public static class Builder {

        private String protocol;
        private String hostname;
        private String urlPath;

        private String rabbitMQHost;
        private String rabbitMQUsername;
        private String rabbitMQPassword;

        private String ftpHost;
        private String ftpUsername;
        private String ftpPassword;
        private int ftpPort;

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder setUrlPath(String urlPath) {
            this.urlPath = urlPath;
            return this;
        }

        public Builder setRabbitMQHost(String rabbitMQHost) {
            this.rabbitMQHost = rabbitMQHost;
            return this;
        }

        public Builder setRabbitMQUsername(String rabbitMQUsername) {
            this.rabbitMQUsername = rabbitMQUsername;
            return this;
        }

        public Builder setRabbitMQPassword(String rabbitMQPassword) {
            this.rabbitMQPassword = rabbitMQPassword;
            return this;
        }

        public Builder setFtpHost(String ftpHost) {
            this.ftpHost = ftpHost;
            return this;
        }

        public Builder setFtpUsername(String ftpUsername) {
            this.ftpUsername = ftpUsername;
            return this;
        }

        public Builder setFtpPassword(String ftpPassword) {
            this.ftpPassword = ftpPassword;
            return this;
        }

        public Builder setFtpPort(int ftpPort) {
            this.ftpPort = ftpPort;
            return this;
        }

        public AsosParser build() {
            AsosParser asosParser = new AsosParser(protocol, hostname, urlPath);
            asosParser.setRabbitMQHost(rabbitMQHost);
            asosParser.setRabbitMQUsername(rabbitMQUsername);
            asosParser.setRabbitMQPassword(rabbitMQPassword);
            asosParser.setFtpHost(ftpHost);
            asosParser.setFtpUsername(ftpUsername);
            asosParser.setFtpPassword(ftpPassword);
            asosParser.setFtpPort(ftpPort);
            return asosParser;
        }

    }

    public List<Product> parse() throws Exception {
        List<Product> products = new ArrayList<>();

        String html = RequestUtils.doGetRequestUsingSocket(hostname, urlPath);
        Document document = Jsoup.parse(html);

        Elements articles = document.select("article.productTile_U0clN");

        RabbitMQPublisher rabbitMQPublisher = null;

        if (!(rabbitMQHost == null || rabbitMQHost.isEmpty() || rabbitMQUsername == null || rabbitMQUsername.isEmpty() || rabbitMQPassword == null))
            rabbitMQPublisher = new RabbitMQPublisher(rabbitMQHost, rabbitMQUsername, rabbitMQPassword);

        for (Element article : articles) {
            Element linkElement = article.selectFirst(".productLink_KM4PI");

            if (linkElement == null) continue;

            String productUrl = linkElement.attr("href");

            String productName = article.select("p.productDescription_sryaw").text();

            String priceLabel = article.select("p#pta-product-" + article.id().substring(8) + "-1").attr("aria-label");

            if(priceLabel.trim().isEmpty()) continue; // some products don't have a price, since they are out of stock or smth else

            String productPrice;
//            System.out.println("price label: " + priceLabel);
            if (priceLabel.contains("current price")) { // first validation, we don't want to get the wrong price
                productPrice = priceLabel.substring(priceLabel.indexOf("current price:")).replaceAll("[^\\d.]", "");
            } else {
                productPrice = priceLabel.substring(priceLabel.indexOf("Original price:")).replaceAll("[^\\d.]", "");
            }

            if(productName.toLowerCase().contains("baggy")) continue; // second validation, I don't quite like baggy clothes

            String productHtml = RequestUtils.doGetRequestUsingSocket(hostname, productUrl.replaceFirst(protocol + hostname, ""));
            Document productDocument = Jsoup.parse(productHtml);

            Element productDetailsElement = productDocument
                    .selectFirst("#productDescriptionDetails .accordion-item-module_content__2cDKX .F_yfF");

            String productDetails = productDetailsElement != null ? productDetailsElement.text() : "";

//            System.out.println("product price: " + productPrice);
            Product product = new Product(productName, productUrl, productDetails, Double.parseDouble(productPrice));
            products.add(product);

            if (rabbitMQPublisher != null)
                rabbitMQPublisher.publishMessage(product.toJson());
        }

        if (rabbitMQPublisher != null)
            rabbitMQPublisher.close();

        File createdFile = createProductsJsonFile(products);
        if (createdFile != null) {
            FTPUploader ftpUploader = new FTPUploader(ftpHost, ftpPort, ftpUsername, ftpPassword);
            ftpUploader.uploadFileToFTP(createdFile);
        }

        return products;
    }

    public File createProductsJsonFile(List<Product> products) {
        File jsonFile = null;
        try {
            String jsonContent = Product.listToJson(products);

            jsonFile = new File("products.json");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
                writer.write(jsonContent);
            }

            System.out.println("Products JSON file created successfully.");
        } catch (IOException e) {
            System.err.println("Error while creating JSON file: " + e.getMessage());
        }

        return jsonFile;
    }


    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRabbitMQHost() {
        return rabbitMQHost;
    }

    public void setRabbitMQHost(String rabbitMQHost) {
        this.rabbitMQHost = rabbitMQHost;
    }

    public String getRabbitMQUsername() {
        return rabbitMQUsername;
    }

    public void setRabbitMQUsername(String rabbitMQUsername) {
        this.rabbitMQUsername = rabbitMQUsername;
    }

    public String getRabbitMQPassword() {
        return rabbitMQPassword;
    }

    public void setRabbitMQPassword(String rabbitMQPassword) {
        this.rabbitMQPassword = rabbitMQPassword;
    }

    public String getFtpHost() {
        return ftpHost;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public void setFtpUsername(String ftpUsername) {
        this.ftpUsername = ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }
}
