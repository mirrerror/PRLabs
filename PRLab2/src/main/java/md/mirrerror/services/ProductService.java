package md.mirrerror.services;

import lombok.RequiredArgsConstructor;
import md.mirrerror.models.Product;
import md.mirrerror.repositories.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAllProductsPaginated(int page, int itemsPerPage) {
        return productRepository.findAll(PageRequest.of(page, itemsPerPage)).getContent();
    }

}
