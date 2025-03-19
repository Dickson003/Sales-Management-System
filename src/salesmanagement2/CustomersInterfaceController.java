package salesmanagement2;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomersInterfaceController {
    
    @FXML
    private Button Logout;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> productidcolumn;

    @FXML
    private TableColumn<Product, String> productNamecolumn;

    @FXML
    private TableColumn<Product, String> descriptioncolumn;

    @FXML
    private TableColumn<Product, String> categorycolumn;

    @FXML
    private TableColumn<Product, String> pricecolumn;

    // You don't include the stock column for the customer interface
    @FXML
    private ListView<String> cart;
    
    @FXML
    private Button checkout;
    
    @FXML
    private Button removeButton;

    private final Map<Product, Integer> cartQuantities = new HashMap<>();
    
    
    private final PaymentService paymentService = new PaymentService();
    
    @FXML
    void handleLogoutAction(ActionEvent event) {
        showExitConfirmationDialog();

    }
      private void showExitConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Customers dashboard");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user clicks OK, close the stage
            Stage stage = (Stage) Logout.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void handleProductSelection(MouseEvent event) {
        // Check if a product is selected
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Check if the quantity in the cart does not exceed the stock
            if (isQuantityValid(selectedProduct)) {
                // Ask the user if they want to add the selected item to the cart
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Add to Cart");
                alert.setHeaderText(null);
                alert.setContentText("Do you want to add '" + selectedProduct.getProductName() + "' to the cart?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Increase quantity or add to cart
                    cartQuantities.merge(selectedProduct, 1, Integer::sum);
                    updateCart();
                }
            } else {
                // Inform the user that the stock is not sufficient
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Insufficient Stock");
                alert.setHeaderText(null);
                alert.setContentText("The selected quantity exceeds the available stock. Please choose a lower quantity.");
                alert.showAndWait();
            }
        }
    }

    private boolean isQuantityValid(Product selectedProduct) {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String selectQuery = "SELECT stock FROM products WHERE product_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            pstmt.setString(1, selectedProduct.getProduct_id());

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                int stock = resultSet.getInt("stock");
                int quantityInCart = cartQuantities.getOrDefault(selectedProduct, 0);

                // Check if the quantity in the cart plus the selected quantity is less than or equal to the stock
                return quantityInCart + 1 <= stock;
            }

            pstmt.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Default to true if an exception occurs
        return true;
    }

    @FXML
    private void increaseQuantity() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Check if the quantity in the cart does not exceed the stock
            if (isQuantityValid(selectedProduct)) {
                cartQuantities.merge(selectedProduct, 1, Integer::sum);
                updateCart();
            } else {
                // Inform the user that the stock is not sufficient
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Insufficient Stock");
                alert.setHeaderText(null);
                alert.setContentText("The selected quantity exceeds the available stock. Please choose a lower quantity.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void decreaseQuantity() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && cartQuantities.containsKey(selectedProduct)) {
            int currentQuantity = cartQuantities.get(selectedProduct);
            if (currentQuantity > 1) {
                cartQuantities.put(selectedProduct, currentQuantity - 1);
            } else {
                cartQuantities.remove(selectedProduct);
            }
            updateCart();
        }
    }
    
    @FXML
    private void removeProductFromCart() {
        String selectedCartItem = cart.getSelectionModel().getSelectedItem();
        if (selectedCartItem != null) {
            // Extract the product name from the selected cart item
            String productName = selectedCartItem.split(" - ")[0];

            // Find the corresponding product in the cartQuantities map
            Product productToRemove = null;
            for (Map.Entry<Product, Integer> entry : cartQuantities.entrySet()) {
                if (entry.getKey().getProductName().equals(productName)) {
                    productToRemove = entry.getKey();
                    break;
                }
            }

            // Remove the product from the cartQuantities map
            if (productToRemove != null) {
                cartQuantities.remove(productToRemove);
                updateCart();
            }
        }
    }
private void updateCart() {
    ObservableList<String> cartItems = FXCollections.observableArrayList();
    double total = 0.0;

    for (Map.Entry<Product, Integer> entry : cartQuantities.entrySet()) {
        Product product = entry.getKey();
        int quantity = entry.getValue();

        // Update the cart with quantity and total price for each product
        String cartItem = String.format("%s - Quantity: %d - Total: Kshs %.2f",
                product.getProductName(), quantity, Double.parseDouble(product.getPrice()) * quantity);

        cartItems.add(cartItem);
        total += Double.parseDouble(product.getPrice()) * quantity;
    }

    // Display the total price in the cart
    cartItems.add(String.format("Total: Kshs %.2f", total));

    cart.setItems(cartItems);
}

@FXML
void handleCheckoutAction(ActionEvent event) {
    ObservableList<String> selectedItems = cart.getSelectionModel().getSelectedItems();
    ObservableList<String> allItems = cart.getItems();

    if (!selectedItems.isEmpty()) {
        // If any items are selected, calculate the total amount
        double totalAmount = calculateTotalAmount(selectedItems);
        // Pass both selected items and total amount to PaymentView
        openPaymentView(selectedItems, totalAmount);
        // After processing payment, remove selected items from the cartQuantities map
        for (String selectedItem : selectedItems) {
            String[] parts = selectedItem.split(" - ");
            String productName = parts[0];
            Product productToRemove = null;
            for (Map.Entry<Product, Integer> entry : cartQuantities.entrySet()) {
                if (entry.getKey().getProductName().equals(productName)) {
                    productToRemove = entry.getKey();
                    break;
                }
            }
            if (productToRemove != null) {
                cartQuantities.remove(productToRemove);
            }
        }
        // Update the cart UI
        updateCart();
    } else {
        // Inform the user that no items are selected
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Items Selected");
        alert.setHeaderText(null);
        alert.setContentText("Please select items in the cart before proceeding to checkout.");
        alert.showAndWait();
    }
}

    private double calculateTotalAmount(ObservableList<String> selectedItems) {
        // Iterate through selected items and calculate the total amount
        double totalAmount = 0.0;

        for (String selectedItem : selectedItems) {
            String[] parts = selectedItem.split(" - ");
            double price = extractNumericValue(parts[2]);

            totalAmount += price;
        }

        return totalAmount;
    }

    private double extractNumericValue(String input) {
        // Extract the numeric part from the input string and parse it into a double
        String numericPart = input.replaceAll("[^\\d.]", "");
        return Double.parseDouble(numericPart);
    }

    private void openPaymentView(ObservableList<String> selectedItems, double totalAmount) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PaymentView.fxml"));
            Parent root = loader.load();

            PaymentViewController paymentController = loader.getController();
            paymentController.setSelectedItems(selectedItems);
            paymentController.setTotalAmount(totalAmount);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Payment");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        // Initialize the TableView columns with the corresponding Product fields
        productidcolumn.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        productNamecolumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        descriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorycolumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        pricecolumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        cart.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Fetch and display products' data in the TableView during initialization
        populateProductTable();
    }

    private void populateProductTable() {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            ObservableList<Product> productsList = FXCollections.observableArrayList();

            // Fetch your product data from the database
            String selectQuery = "SELECT * FROM products";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String product_id = resultSet.getString("product_id");
                String productName = resultSet.getString("product_Name");
                String description = resultSet.getString("description");
                String category = resultSet.getString("Category");
                String price = resultSet.getString("Price");

                // Assuming you have a constructor in your Product class to set these values
                Product product = new Product(product_id, productName, description, category, price, "");

                // Add the product to the list
                productsList.add(product);
            }

            // Populate the TableView with the products data
            productTable.setItems(productsList);

            pstmt.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
