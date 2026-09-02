package code.filipesz.springtransactionengine.services;

import code.filipesz.springtransactionengine.dto.CategorySearchRequest;
import code.filipesz.springtransactionengine.dto.ProductRequest;
import code.filipesz.springtransactionengine.entities.Category;
import code.filipesz.springtransactionengine.entities.Product;
import code.filipesz.springtransactionengine.repositories.CategoryRepository;
import code.filipesz.springtransactionengine.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> productList() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o takim id."));
    }

    public Object getProductOrCategorySummary(CategorySearchRequest request) {
        if (request == null || (request.categoryId() == null && (request.categoryName() == null || request.categoryName().isBlank()))) {
            return categoryRepository.findAllWithProductCount();
        }

        Category category = resolveCategory(request);

        if (category.getId() == null) {
            return Collections.emptyList();
        }

        return productRepository.findByCategory(category);
    }

    @Transactional
    public Product addProduct(ProductRequest request) {
        Category category = resolveCategory(new CategorySearchRequest(request.categoryId(), request.categoryName()));

        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .category(category)
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product editProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o takim id."));

        Category category = resolveCategory(new CategorySearchRequest(request.categoryId(), request.categoryName()));

        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Transactional
    public Product deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o takim id."));
        productRepository.delete(product);
        return product;
    }

    @Transactional
    public Product addStockQuantity(Long id, Integer amount) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o takim id."));
        product.addToStock(amount);
        return productRepository.save(product);
    }

    private Category resolveCategory(CategorySearchRequest request) {
        if (request == null) {
            return null;
        }

        if (request.categoryId() != null) {
            return categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Kategoria o ID " + request.categoryId() + " nie istnieje."));
        }

        if (request.categoryName() != null && !request.categoryName().isBlank()) {
            String trimmedName = request.categoryName().trim();
            return categoryRepository.findByNameIgnoreCase(trimmedName)
                    .orElseGet(() -> Category.builder()
                            .name(trimmedName)
                            .build());
        }

        return null;
    }
}