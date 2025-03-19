package salesmanagement2;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class InventoryController {

    @FXML
    private Button add;

    @FXML
    private Button cancel;

    @FXML
    private TextField category;

    @FXML
    private TableColumn<Product, String> categorycolumn;

    @FXML
    private TextField description;

    @FXML
    private TableColumn<Product, String> descriptioncolumn;

    @FXML
    private Button home;

    @FXML
    private TextField price;

    @FXML
    private TableColumn<Product, String> pricecolumn;

    @FXML
    private TextField productName;

    @FXML
    private TableColumn<Product, String> productNamecolumn;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TextField product_id;

    @FXML
    private TableColumn<Product, String> productidcolumn;

    @FXML
    private Button remove;

    @FXML
    private TextField stock;

    @FXML
    private TableColumn<Product, String> stockcolumn;

    @FXML
    private Button update;

    @FXML
    void initialize() {
        // Initialize the TableView columns with the corresponding Product fields
        productidcolumn.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        productNamecolumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        descriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorycolumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        pricecolumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockcolumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Enable editing for each column
        enableEditingForColumns();

        // Fetch and display products data in the TableView during initialization
        populateProductTable();

        // Add selection listener to populate text fields when a row is selected
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFieldsWithSelectedProduct(newSelection);
            }
        });
    }

    private void populateFieldsWithSelectedProduct(Product selectedProduct) {
        product_id.setText(selectedProduct.getProduct_id());
        productName.setText(selectedProduct.getProductName());
        description.setText(selectedProduct.getDescription());
        category.setText(selectedProduct.getCategory());
        price.setText(selectedProduct.getPrice());
        stock.setText(selectedProduct.getStock());
    }

    private void enableEditingForColumns() {
        productidcolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productidcolumn.setOnEditCommit(this::handleEditCommitProductID);

        productNamecolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productNamecolumn.setOnEditCommit(this::handleEditCommitProductName);

        descriptioncolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptioncolumn.setOnEditCommit(this::handleEditCommitDescription);

        categorycolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        categorycolumn.setOnEditCommit(this::handleEditCommitCategory);

        pricecolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        pricecolumn.setOnEditCommit(this::handleEditCommitPrice);

        stockcolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        stockcolumn.setOnEditCommit(this::handleEditCommitStock);
    }

    @FXML
    private void handleEditCommitProductID(CellEditEvent<Product, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setProduct_id(event.getNewValue());
        updateSelectedProduct(event.getRowValue());
        updateProductInTableView(event.getRowValue());
    }

    @FXML
    private void handleEditCommitProductName(CellEditEvent<Product, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setProductName(event.getNewValue());
        updateSelectedProduct(event.getRowValue());
        updateProductInTableView(event.getRowValue());
    }

    @FXML
    private void handleEditCommitDescription(CellEditEvent<Product, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setDescription(event.getNewValue());
        updateSelectedProduct(event.getRowValue());
        updateProductInTableView(event.getRowValue());
    }

    @FXML
    private void handleEditCommitCategory(CellEditEvent<Product, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setCategory(event.getNewValue());
        updateSelectedProduct(event.getRowValue());
        updateProductInTableView(event.getRowValue());
    }

    @FXML
    private void handleEditCommitPrice(CellEditEvent<Product, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setPrice(event.getNewValue());
        updateSelectedProduct(event.getRowValue());
        updateProductInTableView(event.getRowValue());
    }

    @FXML
    private void handleEditCommitStock(CellEditEvent<Product, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setStock(event.getNewValue());
        updateSelectedProduct(event.getRowValue());
        updateProductInTableView(event.getRowValue());
    }

    private void populateProductTable() {
        List<Product> productList = getProductsFromDatabase();
        productTable.getItems().addAll(productList);
    }

    private List<Product> getProductsFromDatabase() {
        List<Product> productList = new ArrayList<>();

        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            String selectQuery = "SELECT * FROM products";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getString("Product_id"),
                        resultSet.getString("Product_Name"),
                        resultSet.getString("Description"),
                        resultSet.getString("Category"),
                        resultSet.getString("Price"),
                        resultSet.getString("Stock")
                );
                productList.add(product);
            }

            resultSet.close();
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

 @FXML
void handleUpdate() {
    // Get the selected product from the TableView
    Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

    if (selectedProduct != null) {
        // Update the selected product with the new values from the input fields
        updateSelectedProduct(selectedProduct);

        // Update the selected product in the database
        boolean updateSuccessful = updateProductInDatabase(selectedProduct);

        if (updateSuccessful) {
            // Update the data in the TableView only if the database update was successful
            updateProductInTableView(selectedProduct);
            clearFields();
        }
    } else {
        showAlert("No Product Selected", "Please select a product to update.");
    }
}

    private void updateSelectedProduct(Product selectedProduct) {
        selectedProduct.setProduct_id(product_id.getText());
        selectedProduct.setProductName(productName.getText());
        selectedProduct.setDescription(description.getText());
        selectedProduct.setCategory(category.getText());
        selectedProduct.setPrice(price.getText());
        selectedProduct.setStock(stock.getText());
    }

    private void updateProductInTableView(Product updatedProduct) {
        ObservableList<Product> items = productTable.getItems();

        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct_id().equals(updatedProduct.getProduct_id())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            items.set(index, updatedProduct);
        } else {
            items.add(updatedProduct);
        }
    }

private boolean updateProductInDatabase(Product product) {
    try {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        String updateQuery = "UPDATE products SET Product_Name=?, Description=?, Category=?, Price=?, Stock=? WHERE Product_id=?";
        PreparedStatement pstmt = connection.prepareStatement(updateQuery);
        pstmt.setString(1, product.getProductName());
        pstmt.setString(2, product.getDescription());
        pstmt.setString(3, product.getCategory());
        pstmt.setString(4, product.getPrice());
        pstmt.setString(5, product.getStock());
        pstmt.setString(6, product.getProduct_id());

        int rowsUpdated = pstmt.executeUpdate();
        pstmt.close();
        connection.close();

        if (rowsUpdated > 0) {
            showAlert("Update Successful", "Product updated successfully.");
            return true; // Indicate successful update
        } else {
            showAlert("Update Failed", "Failed to update the product.");
            return false; // Indicate failed update
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Indicate failed update
    }
}
@FXML
void handleRemove() {
    Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
    if (selectedProduct != null) {
        // Confirmation dialog before removing the product
        if (showConfirmationDialog("Remove Product", "Are you sure you want to remove this product?")) {
            // Remove the selected product from the database
            removeProductFromDatabase(selectedProduct);
            // Remove the product from the TableView
            productTable.getItems().remove(selectedProduct);
        }
    } else {
        showAlert("No Product Selected", "Please select a product to remove.");
    }
}

    private void removeProductFromDatabase(Product product) {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String deleteQuery = "DELETE FROM products WHERE Product_id=?";
            PreparedStatement pstmt = connection.prepareStatement(deleteQuery);
            pstmt.setString(1, product.getProduct_id());

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Deletion Successful", "Product deleted successfully.");
            } else {
                showAlert("Deletion Failed", "Failed to delete the product.");
            }

            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
@FXML
void handleAdd() {
    // Get values from text fields
    String productId = product_id.getText();
    String productNameText = productName.getText();
    String descriptionText = description.getText();
    String categoryText = category.getText();
    String priceText = price.getText();
    String stockText = stock.getText();

    // Validate input
    if (productId.isEmpty() || productNameText.isEmpty() || descriptionText.isEmpty() ||
            categoryText.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
        showAlert("Incomplete Information", "Please fill in all fields.");
        return;
    }

    // Check if a product with the given Product_id already exists
    if (productAlreadyExists(productId)) {
        showAlert("Duplicate Product ID", "A product with the same Product ID already exists.");
        return;
    }

    // Confirmation dialog before adding the product
    if (showConfirmationDialog("Add Product", "Are you sure you want to add this product?")) {
        // Create a new Product
        Product newProduct = new Product(productId, productNameText, descriptionText, categoryText, priceText, stockText);

        // Add the new product to the database
        addProductToDatabase(newProduct);

        // Add the new product to the TableView
        productTable.getItems().add(newProduct);

        // Clear the input fields
        clearFields();
    }
}

private boolean productAlreadyExists(String productId) {
    for (Product existingProduct : productTable.getItems()) {
        if (existingProduct.getProduct_id().equals(productId)) {
            return true;
        }
    }
    return false;
}
private boolean showConfirmationDialog(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);

    ButtonType yesButton = new ButtonType("Yes");
    ButtonType noButton = new ButtonType("No");

    alert.getButtonTypes().setAll(yesButton, noButton);

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == yesButton;
}
    private void addProductToDatabase(Product product) {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String insertQuery = "INSERT INTO products (Product_id, Product_Name, Description, Category, Price, Stock) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, product.getProduct_id());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getCategory());
            pstmt.setString(5, product.getPrice());
            pstmt.setString(6, product.getStock());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Addition Successful", "Product added successfully.");
            } else {
                showAlert("Addition Failed", "Failed to add the product.");
            }

            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel() {
        // Clear all filled fields
        clearFields();
    }

    private void clearFields() {
        product_id.clear();
        productName.clear();
        description.clear();
        category.clear();
        price.clear();
        stock.clear();
    }

    @FXML
    void handleHome() {
        // Handle navigation back to the AdminInterface.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) home.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
