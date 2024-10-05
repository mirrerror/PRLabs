package md.mirrerror;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AsosParser {

    private String hostname;
    private String urlPath;

    public AsosParser(String hostname, String urlPath) {
        this.hostname = hostname;
        this.urlPath = urlPath;
    }

    public List<Product> parse() {
        List<Product> products = new ArrayList<>();

        String html = RequestUtils.doGetRequestUsingSocket(hostname, urlPath);
        Document document = Jsoup.parse(html);

        Elements articles = document.select("article.productTile_U0clN");

        for (Element article : articles) {
            Element linkElement = article.selectFirst(".productLink_KM4PI");
            String productUrl = linkElement.attr("href");

            String productName = article.select("p.productDescription_sryaw").text();

            String priceLabel = article.select("p#pta-product-" + article.id().substring(8) + "-1").attr("aria-label");

            String productPrice;
            if (priceLabel.contains("current price")) { // first validation, we don't want to parse the wrong price
                productPrice = priceLabel.substring(priceLabel.indexOf("current price:")).replaceAll("[^\\d.]", "");
            } else {
                productPrice = priceLabel.substring(priceLabel.indexOf("Original price:")).replaceAll("[^\\d.]", "");
            }

            if(productName.toLowerCase().contains("baggy")) continue; // second validation, I don't quite like baggy clothes

//                System.out.println("price label: " + priceLabel + "; parsed price: " + productPrice);

            Product product = new Product(productName, productUrl, Double.parseDouble(productPrice));
            products.add(product);
        }


        return products;
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
}
