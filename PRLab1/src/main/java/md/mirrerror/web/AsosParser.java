package md.mirrerror.web;

import md.mirrerror.models.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AsosParser {

    private String protocol;
    private String hostname;
    private String urlPath;

    public AsosParser(String protocol, String hostname, String urlPath) {
        this.protocol = protocol;
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

            if(priceLabel.trim().isEmpty()) continue; // some products don't have a price, since they are out of stock or smth else

            String productPrice;
//            System.out.println("price label: " + priceLabel);
            if (priceLabel.contains("current price")) { // first validation, we don't want to get the wrong price
                productPrice = priceLabel.substring(priceLabel.indexOf("current price:")).replaceAll("[^\\d.]", "");
            } else {
                productPrice = priceLabel.substring(priceLabel.indexOf("Original price:")).replaceAll("[^\\d.]", "");
            }

            if(productName.toLowerCase().contains("baggy")) continue; // second validation, I don't quite like baggy clothes

            String productHtml = RequestUtils.doGetRequestUsingSocket(hostname, productUrl.replaceFirst(protocol + hostname, ""));
            Document productDocument = Jsoup.parse(productHtml);

            Element productDetailsElement = productDocument
                    .selectFirst("#productDescriptionDetails .accordion-item-module_content__2cDKX .F_yfF");

            String productDetails = productDetailsElement != null ? productDetailsElement.text() : "";

//            System.out.println("product price: " + productPrice);
            Product product = new Product(productName, productUrl, productDetails, Double.parseDouble(productPrice));
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
