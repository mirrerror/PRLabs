package md.mirrerror;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AsosParser {

    private String url;

    public AsosParser(String url) {
        this.url = url;
    }

    public List<Product> parse() throws IOException {
        List<Product> products = new ArrayList<>();

        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;

        try {
            URL urlObject = new URL(url);
            connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to connect, HTTP response code: " + connection.getResponseCode());
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }

            String html = response.toString();
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

        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return products;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
