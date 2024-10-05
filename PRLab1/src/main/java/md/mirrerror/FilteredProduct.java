package md.mirrerror;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FilteredProduct extends Product {
    private double priceSumInGbp;
    private double priceSumInMdl;
    private LocalDateTime createdAt;

    public FilteredProduct(String name, String url, double priceInGbp) {
        super(name, url, priceInGbp);
        this.createdAt = LocalDateTime.now();
    }

    public double getPriceSumInGbp() {
        return priceSumInGbp;
    }

    public void setPriceSumInGbp(double priceSumInGbp) {
        this.priceSumInGbp = priceSumInGbp;
    }

    public double getPriceSumInMdl() {
        return priceSumInMdl;
    }

    public void setPriceSumInMdl(double priceSumInMdl) {
        this.priceSumInMdl = priceSumInMdl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toJson() {
        return String.format("{\"name\":\"%s\", \"priceInGbp\":%.2f, \"priceInMdl\":%.2f, \"url\":\"%s\", \"priceSumInGbp\":%.2f, \"priceSumInMdl\":%.2f, \"createdAt\":\"%s\"}",
                getName(), getPriceInGbp(), getPriceInMdl(), getUrl(), priceSumInGbp, priceSumInMdl, createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public String toXml() {
        return String.format("<FilteredProduct><Name>%s</Name><PriceInGbp>%.2f</PriceInGbp><PriceInMdl>%.2f</PriceInMdl><Url>%s</Url><PriceSumInGbp>%.2f</PriceSumInGbp><PriceSumInMdl>%.2f</PriceSumInMdl><CreatedAt>%s</CreatedAt></FilteredProduct>",
                getName(), getPriceInGbp(), getPriceInMdl(), getUrl(), priceSumInGbp, priceSumInMdl, createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public static String filteredProductsListToJson(List<FilteredProduct> filteredProducts) {
        double totalPriceInGbp = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceInGbp).sum();
        double totalPriceInMdl = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceSumInMdl).sum();
        LocalDateTime timestamp = LocalDateTime.now();

        String productJsonList = filteredProducts.stream()
                .map(FilteredProduct::toJson)
                .collect(Collectors.joining(",", "[", "]"));

        return String.format("{\"filteredProducts\":%s, \"totalPriceInGbp\":%.2f, \"totalPriceInMdl\":%.2f, \"timestamp\":\"%s\"}",
                productJsonList, totalPriceInGbp, totalPriceInMdl, timestamp);
    }

    public static String filteredProductsListToXml(List<FilteredProduct> filteredProducts) {
        double totalPriceInGbp = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceInGbp).sum();
        double totalPriceInMdl = filteredProducts.stream().mapToDouble(FilteredProduct::getPriceSumInMdl).sum();
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
        serializedData.append("name=").append(getName()).append(";");
        serializedData.append("priceInGbp=").append(getPriceInGbp()).append(";");
        serializedData.append("priceInMdl=").append(getPriceInMdl()).append(";");
        serializedData.append("url=").append(getUrl()).append(";");
        serializedData.append("priceSumInGbp=").append(priceSumInGbp).append(";");
        serializedData.append("priceSumInMdl=").append(priceSumInMdl).append(";");
        serializedData.append("createdAt=").append(createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(";");
        serializedData.append("|");
        return serializedData.toString().getBytes();
    }

    public static FilteredProduct deserialize(byte[] data) {
        String[] fields = new String(data).split(";");
        String name = null;
        double priceInGbp = 0;
        String url = null;
        double priceSumInGbp = 0;
        double priceSumInMdl = 0;
        LocalDateTime createdAt = null;

        for (String field : fields) {
            if (field.startsWith("name=")) {
                name = field.substring(5);
            } else if (field.startsWith("priceInGbp=")) {
                priceInGbp = Double.parseDouble(field.substring(12));
            } else if (field.startsWith("url=")) {
                url = field.substring(4);
            } else if (field.startsWith("priceSumInGbp=")) {
                priceSumInGbp = Double.parseDouble(field.substring(15));
            } else if (field.startsWith("priceSumInMdl=")) {
                priceSumInMdl = Double.parseDouble(field.substring(15));
            } else if (field.startsWith("createdAt=")) {
                createdAt = LocalDateTime.parse(field.substring(10), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }

        FilteredProduct filteredProduct = new FilteredProduct(name, url, priceInGbp);
        filteredProduct.setPriceSumInGbp(priceSumInGbp);
        filteredProduct.setPriceSumInMdl(priceSumInMdl);
        filteredProduct.createdAt = createdAt;

        return filteredProduct;
    }

    @Override
    public String toString() {
        return "FilteredProduct{" +
                "name='" + getName() + '\'' +
                ", priceInGbp=" + getPriceInGbp() +
                ", priceInMdl=" + getPriceInMdl() +
                ", url='" + getUrl() + '\'' +
                ", priceSumInGbp=" + priceSumInGbp +
                ", priceSumInMdl=" + priceSumInMdl +
                ", createdAt=" + createdAt +
                '}';
    }
}
