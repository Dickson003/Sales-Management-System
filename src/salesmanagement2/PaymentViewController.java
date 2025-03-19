package salesmanagement2;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PaymentViewController {

    @FXML
    private Label totalAmountLabel;
    
    @FXML
    private TextField mpesano;
    
    @FXML
    private Button back;
    
    @FXML
    private Button pay;

    @FXML
    private Label selectedItemsLabel;

    @FXML
    private Button confirmPaymentButton;
    
     @FXML
    private AnchorPane mainAnchorPane;
     
      @FXML
    private ProgressBar paymentProgressBar;

    @FXML
    private Label progressLabel;
    
    @FXML
    void handleBackAction(ActionEvent event) {
          try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomersInterface.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) back.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
 }
    
 @FXML
    public void initialize() {
        paymentProgressBar.setVisible(false); // Initially hide the progress bar
        progressLabel.setVisible(false); // Initially hide the progress label
    }
    private double totalAmount;
    private ObservableList<String> selectedItems;
    private ObservableList<String> cartItems; // Reference to the cart items

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        totalAmountLabel.setText("Total Amount: Kshs " + String.format("%.2f", totalAmount));
    }

    public void setSelectedItems(ObservableList<String> selectedItems) {
        this.selectedItems = selectedItems;
        updateSelectedItemsLabel();
    }
    
  public void setCartItems(ObservableList<String> cartItems) {
        this.cartItems = cartItems;
    }
    private void updateSelectedItemsLabel() {
        if (selectedItems != null && !selectedItems.isEmpty()) {
            StringBuilder itemsText = new StringBuilder("Selected Items:\n");
            for (String item : selectedItems) {
                itemsText.append(item).append("\n");
            }
            selectedItemsLabel.setText(itemsText.toString());
        } else {
            selectedItemsLabel.setText("No items selected.");
        }
    }
private ActionEvent paymentActionEvent;
private Task<String> paymentTask; // Declare paymentTask as a class-level variable

@FXML
void handlePaymentAction(ActionEvent event) {
    // Store the event for later use
    paymentActionEvent = event;

    String safaricomNumber = mpesano.getText();
    if (isValidSafaricomNumber(safaricomNumber)) {
        int mpesaPin = promptForMpesaPin();
        if (mpesaPin == -1) {
            showErrorMessage("Payment Cancelled", "Payment process was cancelled.");
        } else {
            // Display progress bar after valid PIN
            paymentTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    // Simulate payment processing time
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(50); // Simulate processing time for each percentage step
                        updateProgress(i, 100); // Update progress
                        updateMessage(i + "%"); // Update progress message
                    }
                    System.out.println("Payment processed successfully");
                    return "Success";
                }
            };

            paymentTask.setOnSucceeded(workerStateEvent -> {
                String paymentResult = paymentTask.getValue();
                displayPaymentResultAlert(paymentResult); // No need to pass event here
                if ("Success".equals(paymentResult) && cartItems != null && !cartItems.isEmpty()) {
                    cartItems.removeAll(selectedItems);
                }
            });

            new Thread(paymentTask).start();
            displayProgressBar(); // Move the displayProgressBar method call here
        }
    } else {
        showErrorMessage("Invalid Safaricom Number", "Please enter a valid Safaricom number.");
    }
}

private void displayProgressBar() {
    paymentProgressBar.setProgress(0); // Reset progress to 0
    progressLabel.setText("0%"); // Update progress label text
    paymentProgressBar.setVisible(true); // Show the progress bar
    progressLabel.setVisible(true); // Show the progress label

    // Bind the progress property of the progress bar to the task's progress
    paymentProgressBar.progressProperty().bind(paymentTask.progressProperty());

    // Update the progress label text based on the task's progress
    paymentTask.progressProperty().addListener((observable, oldValue, newValue) -> {
        int percentage = (int) (newValue.doubleValue() * 100); // Calculate percentage
        progressLabel.setText(percentage + "%"); // Update progress label text
    });
}

private boolean isValidSafaricomNumber(String number) {
    return number != null && (number.matches("^07\\d{8}$") || number.matches("^01\\d{8}$"));
}


private int promptForMpesaPin() {
    PasswordField pinField = new PasswordField();
    pinField.setPromptText("Enter your 4-digit M-Pesa PIN");

    Alert pinDialog = new Alert(AlertType.CONFIRMATION);
    pinDialog.setTitle("Enter M-Pesa PIN");
    pinDialog.setHeaderText(null);
    pinDialog.getDialogPane().setContent(pinField);

    pinField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue.length() > 4) {
            pinField.setText(oldValue); // Truncate to 4 digits
        }
    });

    Optional<ButtonType> result = pinDialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        String pin = pinField.getText();
        if (isValidMpesaPin(pin)) {
            return Integer.parseInt(pin);
        } else {
            // Show an error message for an invalid PIN
            showErrorMessage("Invalid M-Pesa PIN", "Please enter a valid 4-digit M-Pesa PIN.");
            return -1; // Return a default value to indicate invalid PIN
        }
    } else {
        // Handle the case when the user cancels the dialog
        return -1; // Return a default value to indicate cancel
    }
}
    private boolean isValidMpesaPin(String pin) {
        return pin != null && pin.matches("^\\d{4}$");
    }
private void processPayment(String safaricomNumber, int mpesaPin, ActionEvent event) {
    displayProgressBar();
   Task<String> paymentTask = new Task<String>() {
    @Override
    protected String call() throws Exception {
        // Simulate payment processing time
        for (int i = 0; i <= 100; i++) {
            Thread.sleep(50); // Simulate processing time for each percentage step
            updateProgress(i, 100); // Update progress
            updateMessage(i + "%"); // Update progress message
        }
        System.out.println("Payment processed successfully");
        return "Success";
    }
};

    paymentProgressBar.progressProperty().bind(paymentTask.progressProperty());
    progressLabel.textProperty().bind(paymentTask.messageProperty());

    paymentTask.setOnSucceeded(workerStateEvent -> {
        paymentProgressBar.progressProperty().unbind();
        progressLabel.textProperty().unbind();
        paymentProgressBar.setProgress(1.0); // Set progress to 100% after completion
        progressLabel.setText("100%");
        displayPaymentResultAlert(paymentTask.getValue());
        if ("Success".equals(paymentTask.getValue()) && cartItems != null && !cartItems.isEmpty()) {
            cartItems.removeAll(selectedItems);
        }
    });

    paymentTask.setOnFailed(workerStateEvent -> {
        paymentProgressBar.progressProperty().unbind();
        progressLabel.textProperty().unbind();
        paymentProgressBar.setProgress(0.0); // Reset progress
        progressLabel.setText("0%");
        showErrorMessage("Payment Failed", "Payment processing failed. Please try again.");
    });

    new Thread(paymentTask).start(); // Start the payment task in a new thread
}

private void displayPaymentResultAlert(String result) {
    Alert alert;
    if ("Success".equals(result)) {
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText(null);
        alert.setContentText("Payment processed successfully!");
        saveTransactionDetails();
    } else {
        alert = new Alert(AlertType.ERROR);
        alert.setTitle("Payment Failed");
        alert.setHeaderText(null);
        alert.setContentText("Payment processing failed. Please try again.");
    }

    alert.showAndWait();

    if ("Success".equals(result)) {
        closePaymentView(paymentActionEvent); // Use the stored event here
    }
}

    private void showErrorMessage(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closePaymentView(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void saveTransactionDetails() {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String insertQuery = "INSERT INTO sales (Transaction_id, Invoice_no, Product_id, Product_Name, Quantity, Date, Time, Total_Amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtSales = connection.prepareStatement(insertQuery);

            String updateStockQuery = "UPDATE products SET Stock = Stock - ? WHERE Product_id = ?";
            PreparedStatement pstmtUpdateStock = connection.prepareStatement(updateStockQuery);

            String invoiceNo = generateInvoiceNumber();
            String currentDate = getCurrentDate();
            String currentTime = getCurrentTime();

            for (String selectedItem : selectedItems) {
                String[] parts = selectedItem.split(" - ");
                String productName = parts[0];
                int quantity = Integer.parseInt(parts[1].split(": ")[1]);

                String productId = getProductIdByName(productName);
                double productPrice = getProductPriceById(productId);
                double totalAmount = productPrice * quantity;
                String productTransactionId = generateTransactionId();

                pstmtSales.setString(1, productTransactionId);
                pstmtSales.setString(2, invoiceNo);
                pstmtSales.setString(3, productId);
                pstmtSales.setString(4, productName);
                pstmtSales.setInt(5, quantity);
                pstmtSales.setString(6, currentDate);
                pstmtSales.setString(7, currentTime);
                pstmtSales.setDouble(8, totalAmount);

                pstmtUpdateStock.setInt(1, quantity);
                pstmtUpdateStock.setString(2, productId);

                pstmtSales.executeUpdate();
                pstmtUpdateStock.executeUpdate();
            }

            pstmtSales.close();
            pstmtUpdateStock.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while saving transaction details to the 'sales' table: " + e.getMessage());
        }
    }

    private double getProductPriceById(String productId) {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String selectQuery = "SELECT Price FROM products WHERE Product_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            pstmt.setString(1, productId);

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("Price");
            } else {
                return 0.0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private String generateTransactionId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 8) + "_item";
    }

    private String generateInvoiceNumber() {
        return "INV_" + System.currentTimeMillis();
    }

    private String getCurrentDate() {
        return LocalDate.now().toString();
    }

    private String getCurrentTime() {
        return LocalTime.now().toString();
    }

    private String getProductIdByName(String productName) {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String selectQuery = "SELECT Product_id FROM products WHERE Product_Name = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            pstmt.setString(1, productName);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Product_id");
            }

            pstmt.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
