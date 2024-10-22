package md.mirrerror.models;

import md.mirrerror.utils.CustomSerialization;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Product {

    private static final double GBP_TO_MDL_EXCHANGE_RATE = 22.96;

    private String name;
    private double priceInGbp;
    private double priceInMdl;
    private String url;
    private String productDetails;

    public Product() {}

    public Product(String name, String url, String productDetails, double priceInGbp) {
        this.name = name;
        this.url = url;
        this.productDetails = productDetails;
        setPriceInGbp(priceInGbp);
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
        this.priceInMdl = priceInGbp * GBP_TO_MDL_EXCHANGE_RATE;
    }

    public double getPriceInMdl() {
        return priceInMdl;
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
        return CustomSerialization.serialize(this, this.getClass());
    }

    public static byte[] serializeList(List<Product> products) {
        StringBuilder serializedData = new StringBuilder();
        for (Product product : products) {
            serializedData.append(new String(product.serialize(), StandardCharsets.UTF_8));
        }
        return serializedData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static List<Product> deserializeList(byte[] data) {
        List<Product> productList = new ArrayList<>();
        String[] productStrings = new String(data, StandardCharsets.UTF_8).split("\\|");

        for (String productString : productStrings) {
            if (!productString.isEmpty()) {
                productList.add(Product.deserialize(productString.getBytes(StandardCharsets.UTF_8)));
            }
        }
        return productList;
    }

    public static Product deserialize(byte[] data) {
        return (Product) CustomSerialization.deserialize(new Product(), Product.class, data);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Product product = (Product) object;
        return Double.compare(priceInGbp, product.priceInGbp) == 0 && Objects.equals(name, product.name) && Objects.equals(url, product.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, priceInGbp, url);
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
