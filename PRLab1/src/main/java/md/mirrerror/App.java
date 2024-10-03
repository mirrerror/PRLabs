package md.mirrerror;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        AsosParser asosParser = new AsosParser("https://www.asos.com/men/sale/cat/?cid=8409&ctaref=hp|mw|promo|banner|1|edit|saleupto70offmss");
        List<Product> products = asosParser.parse();

        for(Product product : products) {
            System.out.println(product.getName() + " - " + product.getPriceInGbp() + " GBP - " + product.getPriceInMdl() + " MDL - " + product.getUrl());
        }
    }
}
