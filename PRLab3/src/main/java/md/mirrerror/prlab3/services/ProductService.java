package md.mirrerror.prlab3.services;

import lombok.RequiredArgsConstructor;
import md.mirrerror.prlab3.models.Product;
import md.mirrerror.prlab3.repositories.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Transactional
    public void saveProductList(List<Product> products) {
        String sql = "INSERT INTO products (name, price_in_gbp, url, product_details) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, products, products.size(), (ps, product) -> {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPriceInGbp());
            ps.setString(3, product.getUrl());
            ps.setString(4, product.getProductDetails());
        });
    }

    @Transactional
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAllProductsPaginated(int page, int itemsPerPage) {
        return productRepository.findAll(PageRequest.of(page, itemsPerPage)).getContent();
    }

}
