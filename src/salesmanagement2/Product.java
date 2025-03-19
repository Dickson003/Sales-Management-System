package salesmanagement2;

public class Product {

    private String product_id;
    private String productName;
    private String description;
    private String category;
    private String price;
    private String stock;

    public Product(String product_id, String productName, String description, String category, String price, String stock) {
        this.product_id = product_id;
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Add getters for each property
    public String getProduct_id() {
        return product_id;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String getStock() {
        return stock;
    }

    // Add setters for each property
    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
     @Override
    public String toString() {
        return productName + " - " + price; // Customize this as needed
}
}
