package io.github.springexample.springexample1.service;

import io.github.clientlibrary.client_library.annotation.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @ConfigValue(key = "discount-enabled", defaultValue = "false")
    private boolean discountEnabled;

    @ConfigValue(key = "default-tax", defaultValue = "0")
    private int defaultTax;

    @ConfigValue(key = "discount-percentage", defaultValue = "10")
    private int discountPercentage;

    @ConfigValue(key = "max-discount", defaultValue = "50")
    private double maxDiscount;

    @ConfigValue(key = "store-name", defaultValue = "Loja Exemplo")
    private String storeName;

    @ConfigValue(key = "store-description", defaultValue = "Uma loja de exemplo para testar configurações dinâmicas")
    private String storeDescription;

    @ConfigValue(key = "min-order-value", defaultValue = "25.00")
    private double minOrderValue;

    @ConfigValue(key = "currency", defaultValue = "BRL")
    private String currency;

    public String getConfigStatus() {
        return String.format("Store: %s | Discount: %s (%d%%) | Tax: %d%% | Currency: %s",
                storeName,
                discountEnabled ? "ENABLED" : "DISABLED",
                discountPercentage,
                defaultTax,
                currency);
    }

    public Map<String, Object> getAllConfigurations() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("discount-enabled", discountEnabled);
        configs.put("default-tax", defaultTax);
        configs.put("discount-percentage", discountPercentage);
        configs.put("max-discount", maxDiscount);
        configs.put("store-name", storeName);
        configs.put("store-description", storeDescription);
        configs.put("min-order-value", minOrderValue);
        configs.put("currency", currency);
        configs.put("last-check", LocalDateTime.now());
        return configs;
    }

    public PriceCalculation calculatePrice(double originalPrice) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        double finalPrice = originalPrice;
        double appliedDiscount = 0;
        double appliedTax = 0;

        if (discountEnabled && originalPrice >= minOrderValue) {
            double discountAmount = (originalPrice * discountPercentage) / 100.0;
            appliedDiscount = Math.min(discountAmount, maxDiscount);
            finalPrice -= appliedDiscount;

            log.debug("Discount applied: {}% = {} {} (max: {} {})",
                    discountPercentage, appliedDiscount, currency, maxDiscount, currency);
        }

        if (defaultTax > 0) {
            appliedTax = (finalPrice * defaultTax) / 100.0;
            finalPrice += appliedTax;

            log.debug("Tax applied: {}% = {} {}", defaultTax, appliedTax, currency);
        }

        return new PriceCalculation(
                originalPrice,
                appliedDiscount,
                appliedTax,
                finalPrice,
                currency,
                discountEnabled,
                originalPrice >= minOrderValue
        );
    }

    public Map<String, PriceCalculation> getTestScenarios() {
        Map<String, PriceCalculation> scenarios = new HashMap<>();

        scenarios.put("low-value-order", calculatePrice(15.0));
        scenarios.put("min-order-value", calculatePrice(minOrderValue));
        scenarios.put("normal-order", calculatePrice(100.0));
        scenarios.put("high-value-order", calculatePrice(500.0));
        scenarios.put("zero-value", calculatePrice(0.0));

        return scenarios;
    }

    public static class PriceCalculation {
        private final double originalPrice;
        private final double appliedDiscount;
        private final double appliedTax;
        private final double finalPrice;
        private final String currency;
        private final boolean discountEnabled;
        private final boolean qualifiesForDiscount;

        public PriceCalculation(double originalPrice, double appliedDiscount, double appliedTax,
                                double finalPrice, String currency, boolean discountEnabled,
                                boolean qualifiesForDiscount) {
            this.originalPrice = originalPrice;
            this.appliedDiscount = appliedDiscount;
            this.appliedTax = appliedTax;
            this.finalPrice = finalPrice;
            this.currency = currency;
            this.discountEnabled = discountEnabled;
            this.qualifiesForDiscount = qualifiesForDiscount;
        }

        public double getOriginalPrice() { return originalPrice; }
        public double getAppliedDiscount() { return appliedDiscount; }
        public double getAppliedTax() { return appliedTax; }
        public double getFinalPrice() { return finalPrice; }
        public String getCurrency() { return currency; }
        public boolean isDiscountEnabled() { return discountEnabled; }
        public boolean isQualifiesForDiscount() { return qualifiesForDiscount; }

        public double getSavings() { return appliedDiscount; }

        public String getFormattedSummary() {
            return String.format("Original: %.2f %s | Discount: %.2f %s | Tax: %.2f %s | Final: %.2f %s",
                    originalPrice, currency, appliedDiscount, currency, appliedTax, currency, finalPrice, currency);
        }

        @Override
        public String toString() {
            return getFormattedSummary();
        }
    }
}
