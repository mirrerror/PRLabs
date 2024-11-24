package md.mirrerror;

import md.mirrerror.filters.ProductFilter;
import md.mirrerror.models.FilteredProduct;
import md.mirrerror.models.Product;
import md.mirrerror.web.AsosParser;

import java.util.List;

public class Main {

    private static final String PROTOCOL = "https://";
    private static final String HOSTNAME = "www.asos.com";
    private static final String URL_PATH = "/men/sale/cat/?cid=8409&ctaref=hp|mw|promo|banner|1|edit|saleupto70offmss";
//    private static final String FULL_URL = PROTOCOL + HOSTNAME + URL_PATH;

    private static final String RABBITMQ_HOST = "localhost";
    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "guest";

    public static void main(String[] args) throws Exception {
//        System.out.println("GET request to asos.com:\n");
//        System.out.println(RequestUtils.doGetRequest(FULL_URL));
//
//        System.out.print("\n\n\n");
//
//        System.out.println("GET request to asos.com using socket:\n");
//        System.out.println(RequestUtils.doGetRequestUsingSocket(HOSTNAME, URL_PATH));
//
//        System.out.print("\n\n\n");

        AsosParser asosParser = new AsosParser(PROTOCOL, HOSTNAME, URL_PATH, RABBITMQ_HOST, RABBITMQ_USERNAME, RABBITMQ_PASSWORD);
        ProductFilter productFilter = new ProductFilter();

        List<Product> products = asosParser.parse();
        List<FilteredProduct> filteredProducts = productFilter.selectProducts(products, 10, 50);

        List<Product> fiveFirstProducts = products.subList(0, 5);
        List<FilteredProduct> fiveFirstFilteredProducts = filteredProducts.subList(0, 5);

        System.out.println(Product.listToJson(fiveFirstProducts));
        System.out.print("\n\n");
        System.out.println(Product.listToXml(fiveFirstProducts));

        System.out.print("\n\n\n");

        System.out.println(FilteredProduct.filteredProductsListToJson(fiveFirstFilteredProducts));
        System.out.print("\n\n");
        System.out.println(FilteredProduct.filteredProductsListToXml(fiveFirstFilteredProducts));

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
        byte[] serializedProduct = products.get(0).serialize();
        byte[] serializedFilteredProduct = filteredProducts.get(0).serialize();

        System.out.print(new String(serializedProduct));
        System.out.print("\n\n");
        System.out.print(Product.deserialize(serializedProduct));
        System.out.print("\n\n");
        System.out.println(new String(serializedFilteredProduct));
        System.out.print("\n\n");
        System.out.print(FilteredProduct.deserialize(filteredProducts.get(0).serialize()));

        System.out.println("\n\n\n");

        System.out.println("Custom serialization and deserialization for lists:\n");
        byte[] serializedProducts = Product.serializeList(fiveFirstProducts);
        System.out.print(new String(serializedProducts));
        System.out.print("\n\n");
        System.out.print(Product.deserializeList(serializedProducts));
        System.out.print(FilteredProduct.deserialize(serializedFilteredProduct));
    }

}
