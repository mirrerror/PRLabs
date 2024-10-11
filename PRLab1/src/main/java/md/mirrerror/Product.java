package md.mirrerror;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        CustomSerialization.serializeFields(this, serializedData, this.getClass());
        serializedData.append("|");
        return serializedData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static Product deserialize(byte[] data) {
        Map<String, String> fieldValues = new HashMap<>();
        String[] fields = new String(data, StandardCharsets.UTF_8).split(";");

        for (String field : fields) {
            if (!field.equals("|") && field.contains("=")) {
                String[] keyValue = field.split("=", 2);
                if (keyValue.length == 2) {
                    fieldValues.put(keyValue[0], keyValue[1]);
                }
            }
        }

        try {
            Product product = new Product(
                    fieldValues.get("name"),
                    fieldValues.get("url"),
                    fieldValues.get("productDetails"),
                    Double.parseDouble(fieldValues.get("priceInGbp"))
            );

            CustomSerialization.deserializeFields(product, fieldValues, product.getClass());

            return product;

        } catch (Exception e) {
            throw new RuntimeException("Error during deserialization: " + e.getMessage(), e);
        }
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
