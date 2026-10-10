package code.filipesz.springtransactionengine.controllers;

import code.filipesz.springtransactionengine.dto.CategorySearchRequest;
import code.filipesz.springtransactionengine.dto.ProductRequest;
import code.filipesz.springtransactionengine.entities.Product;
import code.filipesz.springtransactionengine.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> productList() {
        return ResponseEntity.ok(productService.productList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getProductsByCategory(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categoryName) {

        CategorySearchRequest request = new CategorySearchRequest(categoryId, categoryName);
        return ResponseEntity.ok(productService.getProductOrCategorySummary(request));
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.addProduct(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Product> editProduct(@Valid @PathVariable Long id, ProductRequest request) {
        return ResponseEntity.ok(productService.editProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stock")
    public ResponseEntity<Product> addStockQuantity(@PathVariable Long id, @RequestParam(required = true) Integer amount) {
        return ResponseEntity.ok(productService.addStockQuantity(id, amount));
    }
}