package md.mirrerror;

public class Product {

    private String name;
    private double priceInGbp;
    private double priceInMdl;
    private String url;

    public Product(String name, String url, double priceInGbp) {
        this.name = name;
        this.url = url;
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
}
