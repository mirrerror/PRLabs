package md.mirrerror;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilteredProduct extends Product {
    private LocalDateTime createdAt;

    public FilteredProduct(String name, String url, String productDetails, double priceInGbp) {
        super(name, url, productDetails, priceInGbp);
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toJson() {
        return String.format("{\"name\":\"%s\", \"priceInGbp\":%.2f, \"priceInMdl\":%.2f, \"url\":\"%s\", \"productDetails\":\"%s\", \"createdAt\":\"%s\"}",
                getName(), getPriceInGbp(), getPriceInMdl(), getUrl(), getProductDetails(), createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public String toXml() {
        return String.format("<FilteredProduct><Name>%s</Name><PriceInGbp>%.2f</PriceInGbp><PriceInMdl>%.2f</PriceInMdl><Url>%s</Url><ProductDetails>%s</ProductDetails><CreatedAt>%s</CreatedAt></FilteredProduct>",
                getName(), getPriceInGbp(), getPriceInMdl(), getUrl(), getProductDetails().replace("&", "&amp;"), createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public static String filteredProductsListToJson(List<FilteredProduct> filteredProducts) {
        double totalPriceInGbp = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceInGbp).sum();
        double totalPriceInMdl = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceInMdl).sum();
        LocalDateTime timestamp = LocalDateTime.now();

        String productJsonList = filteredProducts.stream()
                .map(FilteredProduct::toJson)
                .collect(Collectors.joining(",", "[", "]"));

        return String.format("{\"filteredProducts\":%s, \"totalPriceInGbp\":%.2f, \"totalPriceInMdl\":%.2f, \"timestamp\":\"%s\"}",
                productJsonList, totalPriceInGbp, totalPriceInMdl, timestamp);
    }

    public static String filteredProductsListToXml(List<FilteredProduct> filteredProducts) {
        double totalPriceInGbp = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceInGbp).sum();
        double totalPriceInMdl = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceInMdl).sum();
        LocalDateTime timestamp = LocalDateTime.now();

        String productXmlList = filteredProducts.stream()
                .map(FilteredProduct::toXml)
                .collect(Collectors.joining());

        return String.format("<FilteredProducts><TotalPriceInGbp>%.2f</TotalPriceInGbp><TotalPriceInMdl>%.2f</TotalPriceInMdl><Timestamp>%s</Timestamp>%s</FilteredProducts>",
                totalPriceInGbp, totalPriceInMdl, timestamp, productXmlList);
    }

    @Override
    public byte[] serialize() {
        StringBuilder serializedData = new StringBuilder();
        CustomSerialization.serializeFields(this, serializedData, this.getClass());
        serializedData.append("|");
        return serializedData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static FilteredProduct deserialize(byte[] data) {
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
            FilteredProduct filteredProduct = new FilteredProduct(
                    fieldValues.get("name"),
                    fieldValues.get("url"),
                    fieldValues.get("productDetails"),
                    Double.parseDouble(fieldValues.get("priceInGbp"))
            );

            CustomSerialization.deserializeFields(filteredProduct, fieldValues, filteredProduct.getClass());

            return filteredProduct;

        } catch (Exception e) {
            throw new RuntimeException("Error during deserialization: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "FilteredProduct{" +
                "name='" + getName() + '\'' +
                ", priceInGbp=" + getPriceInGbp() +
                ", priceInMdl=" + getPriceInMdl() +
                ", url='" + getUrl() + '\'' +
                ", productDetails='" + getProductDetails() + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
