package md.mirrerror;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        AsosParser asosParser = new AsosParser("https://www.asos.com/men/sale/cat/?cid=8409&ctaref=hp|mw|promo|banner|1|edit|saleupto70offmss");
        List<Product> products = asosParser.parse();

        System.out.println("All the products:");
        for(Product product : products) {
            System.out.println(product.getName() + " - " + product.getPriceInGbp() + " GBP - " + product.getPriceInMdl() + " MDL - " + product.getUrl());
        }

        System.out.print("\n\n\n");

        ProductFilter productFilter = new ProductFilter();
        List<FilteredProduct> filteredProducts = productFilter.selectProducts(products, 10, 50);

        System.out.println("Filtered products:");
        for(FilteredProduct filteredProduct : filteredProducts) {
            System.out.println(filteredProduct.getName() + " - " + filteredProduct.getPriceInGbp() + " GBP - " + filteredProduct.getPriceInMdl() + " MDL - " + filteredProduct.getUrl() + " - " + filteredProduct.getCreatedAt());
        }
    }
}
