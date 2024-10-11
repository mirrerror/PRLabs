package md.mirrerror;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilteredProduct extends Product {

    private LocalDateTime createdAt;

    public FilteredProduct() {}

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

    public static FilteredProduct deserialize(byte[] data) {
        return (FilteredProduct) CustomSerialization.deserialize(new FilteredProduct(), FilteredProduct.class, data);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FilteredProduct product = (FilteredProduct) object;
        return Double.compare(super.getPriceInGbp(), product.getPriceInGbp()) == 0
                && Objects.equals(super.getName(), product.getName())
                && Objects.equals(super.getUrl(), product.getUrl())
                && Objects.equals(createdAt, product.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getName(), super.getPriceInGbp(), super.getUrl(), createdAt);
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
