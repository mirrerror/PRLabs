package md.mirrerror.filters;

import md.mirrerror.models.FilteredProduct;
import md.mirrerror.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductFilter {

    public List<FilteredProduct> selectProducts(List<Product> products, double minPrice, double maxPrice) {
        List<FilteredProduct> filteredProducts = new ArrayList<>();

        for(Product product : products) {
            if(product.getPriceInGbp() >= minPrice && product.getPriceInGbp() <= maxPrice) {
                filteredProducts.add(new FilteredProduct(product.getName(), product.getUrl(), product.getProductDetails(), product.getPriceInGbp()));
            }
        }

        return filteredProducts;
    }

}
