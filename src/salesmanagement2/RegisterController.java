package salesmanagement2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.ResultSet;

public class RegisterController {

    @FXML
    private ComboBox<String> accountType;

    @FXML
    private Button cancel;

    @FXML
    private TextField email;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private Button ok;

    @FXML
    private Button back;
    
    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private ToggleGroup genderToggleGroup;

    private DatabaseConnection databaseConnection; // Add a reference to the DatabaseConnection

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void initialize() {
        databaseConnection = new DatabaseConnection();// Initialize the DatabaseConnection
        // Set the radio buttons to use the ToggleGroup
        male.setToggleGroup(genderToggleGroup);
        female.setToggleGroup(genderToggleGroup);

        // Set the options for the account type combo box
        accountType.getItems().addAll("ADMIN", "USER");
    }
@FXML
void handleOKButtonAction(ActionEvent event) {
    if (validateFields()) {
        if (saveUserData()) {
            showAlert("Success", "User data saved successfully! Now you can log in.");
            
            // Load the login scene
            loadLoginScene();
            
            // Close the current window
            closeCurrentWindow();
        } else {
            showAlert("Error", "Failed to save user data. Please try again.");
        }
    }
}

private void closeCurrentWindow() {
    // Get the stage associated with any UI element of the current window
    Stage stage = (Stage) ok.getScene().getWindow();
    stage.close();
}
    @FXML
    void handleCancelButtonAction(ActionEvent event) {
        // Reset or clear the fields when the CANCEL button is clicked
        username.clear();
        password.clear();
        genderToggleGroup.selectToggle(null); // Clear the selected toggle
        email.clear();
        accountType.getSelectionModel().clearSelection();
    }
@FXML
    void handleBackAction(ActionEvent event) {
loadLoginScene();
    }
    
   @FXML
    void handleaccountTypeButtonAction(ActionEvent event) {
        // Handle the action for accountType selection if needed
    }
    
    private boolean saveUserData() {
        try {
            Connection connection = databaseConnection.getConnection();

            // Check if the username already exists
            if (isUsernameExists(connection)) {
                showAlert("Error", "Username already exists. Please choose a different one.");
                return false;
            }

            // Check if an ADMIN already exists
            if (isExistingAdmin(connection)) {
                showAlert("Error", "An ADMIN already exists. Only one ADMIN is allowed.");
                return false;
            }

            String insertQuery = "INSERT INTO users (username, password, Gender, EmailAddress, account_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);

            pstmt.setString(1, username.getText());
            pstmt.setString(2, password.getText());
            pstmt.setString(3, getSelectedGender());
            pstmt.setString(4, email.getText());
            pstmt.setString(5, accountType.getValue());

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isUsernameExists(Connection connection) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username.getText()); // Add this line to check for a specific username
        
        ResultSet resultSet = pstmt.executeQuery();
        
        boolean usernameExists = resultSet.next();
        
        resultSet.close();
        pstmt.close();
        
        return usernameExists;
    }

    private boolean isExistingAdmin(Connection connection) throws SQLException {
        if ("ADMIN".equals(accountType.getValue())) {
            String query = "SELECT * FROM users WHERE account_type = 'ADMIN'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            
            ResultSet resultSet = pstmt.executeQuery();
            
            boolean adminExists = resultSet.next();
            
            resultSet.close();
            pstmt.close();
            
            return adminExists;
        }
        
        return false;
    }
    
    private String getSelectedGender() {
        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        return (selectedRadioButton != null) ? selectedRadioButton.getText() : "";
    }

private boolean validateFields() {
    if (username.getText().isEmpty() || password.getText().isEmpty() || genderToggleGroup.getSelectedToggle() == null
            || email.getText().isEmpty() || accountType.getValue() == null) {
        showAlert("Error", "Please fill in all the fields.");
        return false;
    }

    // Validate username format
    if (!isValidUsername(username.getText())) {
        showAlert("Error", "Invalid username format. Username should start with a capital letter and contain only letters and digits (no spaces).");
        return false;
    }

    // Validate email format
    if (!isValidEmail(email.getText())) {
        showAlert("Error", "Invalid email address. Please enter a valid email.");
        return false;
    }

    return true;
}

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
private boolean isValidUsername(String username) {
    // Check if the username contains only letters, digits, and starts with a capital letter
    String usernameRegex = "^[A-Z][a-zA-Z0-9]*$";
    return username.matches(usernameRegex);
}

    private boolean isValidEmail(String email) {
        // Use a simple regex to check for a valid email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

 private void loadLoginScene() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Get the stage from the back button's event
        Stage stage = (Stage) back.getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Error loading Login scene.");
    }
}
}
