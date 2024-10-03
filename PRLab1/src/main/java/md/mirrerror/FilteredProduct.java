package md.mirrerror;

import java.time.LocalDateTime;

public class FilteredProduct extends Product {

    private double priceSumInGbp;
    private double priceSumInMdl;
    private final LocalDateTime createdAt;

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
}
