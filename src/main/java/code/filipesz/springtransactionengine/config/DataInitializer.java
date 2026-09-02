package code.filipesz.springtransactionengine.config;

import code.filipesz.springtransactionengine.entities.Category;
import code.filipesz.springtransactionengine.entities.Product;
import code.filipesz.springtransactionengine.repositories.CategoryRepository;
import code.filipesz.springtransactionengine.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        Category category1 = Category.builder()
                .name("Urządzenia wskazujące")
                .build();

        Category category2 = Category.builder()
                .name("Klawiatury")
                .build();

        categoryRepository.saveAll(List.of(category1, category2));

        Product product1 = Product.builder()
                .category(category1)
                .name("Mysz Optyczna")
                .price(new BigDecimal("199.99"))
                .stockQuantity(20)
                .build();

        Product product2 = Product.builder()
                .category(category2)
                .name("Klawiatura Mechaniczna")
                .price(new BigDecimal("349.99"))
                .stockQuantity(15)
                .build();

        productRepository.saveAll(List.of(product1, product2));
    }
}