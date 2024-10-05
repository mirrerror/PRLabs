package md.mirrerror;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Product {
    private String name;
    private double priceInGbp;
    private double priceInMdl;
    private String url;
    private String productDetails;

    public Product(String name, String url, String productDetails, double priceInGbp) {
        this.name = name;
        this.url = url;
        this.productDetails = productDetails;
        this.priceInGbp = priceInGbp;
        this.priceInMdl = priceInGbp * 22.96;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceInGbp() {
        return priceInGbp;
    }

    public void setPriceInGbp(double priceInGbp) {
        this.priceInGbp = priceInGbp;
    }

    public double getPriceInMdl() {
        return priceInMdl;
    }

    public void setPriceInMdl(double priceInMdl) {
        this.priceInMdl = priceInMdl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public String toJson() {
        return String.format("{\"name\":\"%s\", \"priceInGbp\":%.2f, \"priceInMdl\":%.2f, \"url\":\"%s\", \"productDetails\":\"%s\"}",
                name, priceInGbp, priceInMdl, url, productDetails);
    }

    public String toXml() {
        return String.format("<Product><Name>%s</Name><PriceInGbp>%.2f</PriceInGbp><PriceInMdl>%.2f</PriceInMdl><Url>%s</Url><ProductDetails>%s</ProductDetails></Product>",
                name, priceInGbp, priceInMdl, url, productDetails.replace("&", "&amp;"));
    }

    public static String listToJson(List<Product> products) {
        double totalPriceInGbp = products.stream().mapToDouble(Product::getPriceInGbp).sum();
        double totalPriceInMdl = products.stream().mapToDouble(Product::getPriceInMdl).sum();
        LocalDateTime timestamp = LocalDateTime.now();

        String productJsonList = products.stream()
                .map(Product::toJson)
                .collect(Collectors.joining(",", "[", "]"));

        return String.format("{\"products\":%s, \"totalPriceInGbp\":%.2f, \"totalPriceInMdl\":%.2f, \"timestamp\":\"%s\"}",
                productJsonList, totalPriceInGbp, totalPriceInMdl, timestamp);
    }

    public static String listToXml(List<Product> products) {
        double totalPriceInGbp = products.stream().mapToDouble(Product::getPriceInGbp).sum();
        double totalPriceInMdl = products.stream().mapToDouble(Product::getPriceInMdl).sum();
        LocalDateTime timestamp = LocalDateTime.now();

        String productXmlList = products.stream()
                .map(Product::toXml)
                .collect(Collectors.joining());

        return String.format("<Products><TotalPriceInGbp>%.2f</TotalPriceInGbp><TotalPriceInMdl>%.2f</TotalPriceInMdl><Timestamp>%s</Timestamp>%s</Products>",
                totalPriceInGbp, totalPriceInMdl, timestamp, productXmlList);
    }

    public byte[] serialize() {
        StringBuilder serializedData = new StringBuilder();
        serializedData.append("name=").append(name).append(";");
        serializedData.append("priceInGbp=").append(priceInGbp).append(";");
        serializedData.append("priceInMdl=").append(priceInMdl).append(";");
        serializedData.append("url=").append(url).append(";");
        serializedData.append("productDetails=").append(productDetails).append(";");
        serializedData.append("|");
        return serializedData.toString().getBytes();
    }

    public static Product deserialize(byte[] data) {
        String[] fields = new String(data).split(";");
        String name = null;
        double priceInGbp = 0;
        String url = null;
        String productDetails = null;

        for (String field : fields) {
            if (field.startsWith("name=")) {
                name = field.substring(5);
            } else if (field.startsWith("priceInGbp=")) {
                priceInGbp = Double.parseDouble(field.substring(12));
            } else if (field.startsWith("url=")) {
                url = field.substring(4);
            } else if (field.startsWith("productDetails=")) {
                productDetails = field.substring(15);
            }
        }

        return new Product(name, url, productDetails, priceInGbp);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", priceInGbp=" + priceInGbp +
                ", priceInMdl=" + priceInMdl +
                ", url='" + url + '\'' +
                ", productDetails='" + productDetails + '\'' +
                '}';
    }
}
