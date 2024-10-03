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

    public String toJson() {
        return String.format("{\"name\":\"%s\", \"priceInGbp\":%.2f, \"priceInMdl\":%.2f, \"url\":\"%s\"}",
                name, priceInGbp, priceInMdl, url);
    }

    public String toXml() {
        return String.format("<Product><Name>%s</Name><PriceInGbp>%.2f</PriceInGbp><PriceInMdl>%.2f</PriceInMdl><Url>%s</Url></Product>",
                name, priceInGbp, priceInMdl, url);
    }

    public byte[] serialize() {
        StringBuilder serializedData = new StringBuilder();
        serializedData.append("name=").append(name).append(";");
        serializedData.append("priceInGbp=").append(priceInGbp).append(";");
        serializedData.append("priceInMdl=").append(priceInMdl).append(";");
        serializedData.append("url=").append(url).append(";");
        serializedData.append("|");
        return serializedData.toString().getBytes();
    }

    public static Product deserialize(byte[] data) {
        String[] fields = new String(data).split(";");
        String name = null;
        double priceInGbp = 0;
        String url = null;

        for (String field : fields) {
            if (field.startsWith("name=")) {
                name = field.substring(5);
            } else if (field.startsWith("priceInGbp=")) {
                priceInGbp = Double.parseDouble(field.substring(12));
            } else if (field.startsWith("url=")) {
                url = field.substring(4);
            }
        }

        return new Product(name, url, priceInGbp);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", priceInGbp=" + priceInGbp +
                ", priceInMdl=" + priceInMdl +
                ", url='" + url + '\'' +
                '}';
    }
}
