package example.storage;

import example.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InventoryService {
    private static final InventoryService INSTANCE = new InventoryService();

    private final ConcurrentMap<String, Product> storeProducts = new ConcurrentHashMap<>();

    private InventoryService() {

        storeProducts.put("Apple", new Product("Apple", 100, 25.50, "Fruits"));
        storeProducts.put("Banana", new Product("Banana", 150, 30.00, "Fruits"));
    }

    public static InventoryService getInstance() {
        return INSTANCE;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(storeProducts.values());
    }

    public String addProduct(Product product) {
        storeProducts.put(product.getItemName(), product);
        return "SUCCESS: Product " + product.getItemName() + " added.";
    }
}