package md.mirrerror;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        AsosParser asosParser = new AsosParser("https://www.asos.com/men/sale/cat/?cid=8409&ctaref=hp|mw|promo|banner|1|edit|saleupto70offmss");
        List<Product> products = asosParser.parse();

        System.out.println("All the products:\n");
        for(Product product : products) {
            System.out.println(product.getName() + " - " + product.getPriceInGbp() + " GBP - " + product.getPriceInMdl() + " MDL - " + product.getUrl());
        }

        System.out.print("\n\n\n");

        ProductFilter productFilter = new ProductFilter();
        List<FilteredProduct> filteredProducts = productFilter.selectProducts(products, 10, 50);

        System.out.println("Filtered products:\n");
        for(FilteredProduct filteredProduct : filteredProducts) {
            System.out.println(filteredProduct.getName() + " - " + filteredProduct.getPriceInGbp() + " GBP - " + filteredProduct.getPriceInMdl() + " MDL - " + filteredProduct.getUrl() + " - " + filteredProduct.getCreatedAt());
        }

        System.out.print("\n\n\n");

        System.out.println("GET request to asos.com:\n");
        RequestUtils.doGetRequest();

        System.out.print("\n\n\n");

        System.out.println("GET request to asos.com using socket:\n");
        RequestUtils.doGetRequestUsingSocket();

        System.out.print("\n\n\n");

        System.out.println("Serialization:\n");
        System.out.print(products.get(0).toJson());
        System.out.print("\n\n");
        System.out.print(products.get(0).toXml());
        System.out.print("\n\n");
        System.out.println(filteredProducts.get(0).toJson());
        System.out.print("\n\n");
        System.out.print(filteredProducts.get(0).toXml());

        System.out.print("\n\n\n");

        System.out.println("Custom serialization and deserialization:\n");
        System.out.print(new String(products.get(0).serialize()));
        System.out.print("\n\n");
        System.out.print(Product.deserialize(products.get(0).serialize()));
        System.out.print("\n\n");
        System.out.println(new String(filteredProducts.get(0).serialize()));
        System.out.print("\n\n");
        System.out.print(FilteredProduct.deserialize(filteredProducts.get(0).serialize()));
    }

}
