package io.github.springexample.springexample1.controller;

import io.github.springexample.springexample1.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/config-status")
    public ResponseEntity<String> getConfigStatus() {
        return ResponseEntity.ok(productService.getConfigStatus());
    }

    @GetMapping("/configs")
    public ResponseEntity<Map<String, Object>> getAllConfigurations() {
        return ResponseEntity.ok(productService.getAllConfigurations());
    }

    @GetMapping("/calculate-price")
    public ResponseEntity<ProductService.PriceCalculation> calculatePrice(
            @RequestParam(defaultValue = "100.0") double price) {
        try {
            ProductService.PriceCalculation calculation = productService.calculatePrice(price);
            return ResponseEntity.ok(calculation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
