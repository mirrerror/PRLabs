package md.mirrerror;

import java.util.List;

public class Main {

    private static final String PROTOCOL = "https://";
    private static final String HOSTNAME = "www.asos.com";
    private static final String URL_PATH = "/men/sale/cat/?cid=8409&ctaref=hp|mw|promo|banner|1|edit|saleupto70offmss";
    private static final String FULL_URL = PROTOCOL + HOSTNAME + URL_PATH;

    public static void main(String[] args) {
//        System.out.println("GET request to asos.com:\n");
//        System.out.println(RequestUtils.doGetRequest(FULL_URL));
//
//        System.out.print("\n\n\n");
//
//        System.out.println("GET request to asos.com using socket:\n");
//        System.out.println(RequestUtils.doGetRequestUsingSocket(HOSTNAME, URL_PATH));
//
//        System.out.print("\n\n\n");

        AsosParser asosParser = new AsosParser(PROTOCOL, HOSTNAME, URL_PATH);
        List<Product> products = asosParser.parse();
        List<Product> fiveFirstProducts = products.subList(0, 5);

        System.out.println(Product.listToJson(fiveFirstProducts));
        System.out.print("\n\n");
        System.out.println(Product.listToXml(fiveFirstProducts));

        System.out.print("\n\n\n");

        ProductFilter productFilter = new ProductFilter();
        List<FilteredProduct> filteredProducts = productFilter.selectProducts(products, 10, 50);
        List<FilteredProduct> fiveFirstFilteredProducts = filteredProducts.subList(0, 5);

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
        System.out.print(new String(products.get(0).serialize()));
        System.out.print("\n\n");
        System.out.print(Product.deserialize(products.get(0).serialize()));
        System.out.print("\n\n");
        System.out.println(new String(filteredProducts.get(0).serialize()));
        System.out.print("\n\n");
        System.out.print(FilteredProduct.deserialize(filteredProducts.get(0).serialize()));

        System.out.println("\n\n\n");

        System.out.println("Custom serialization and deserialization for lists:\n");
        byte[] serializedProducts = Product.serializeList(fiveFirstProducts);
        System.out.print(new String(serializedProducts));
        System.out.print("\n\n");
        System.out.print(Product.deserializeList(serializedProducts));
    }

}
