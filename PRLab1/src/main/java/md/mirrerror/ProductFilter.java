package md.mirrerror;

import java.util.ArrayList;
import java.util.List;

public class ProductFilter {

    public List<FilteredProduct> selectProducts(List<Product> products, double minPrice, double maxPrice) {
        List<FilteredProduct> filteredProducts = new ArrayList<>();

        double sumInGbp = sumPrices(products);
        double sumInMdl = sumPricesInMdl(products);

        for(Product product : products) {
            if(product.getPriceInGbp() >= minPrice && product.getPriceInGbp() <= maxPrice) {
                FilteredProduct filteredProduct = new FilteredProduct(product.getName(), product.getUrl(), product.getPriceInGbp());
                filteredProduct.setPriceSumInGbp(sumInGbp);
                filteredProduct.setPriceSumInMdl(sumInMdl);
                filteredProducts.add(filteredProduct);
            }
        }

        return filteredProducts;
    }

    public double sumPrices(List<Product> products) {
        double sum = 0;

        for(Product product : products) {
            sum += product.getPriceInGbp();
        }

        return sum;
    }

    public double sumPricesInMdl(List<Product> products) {
        double sum = 0;

        for(Product product : products) {
            sum += product.getPriceInMdl();
        }

        return sum;
    }

}
