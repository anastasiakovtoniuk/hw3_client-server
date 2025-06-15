package example.model;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String itemName;
    private int quantity;
    private final double price;
    private final String category;

    public Product(String itemName, int quantity, double price, String category) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }


    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }
}