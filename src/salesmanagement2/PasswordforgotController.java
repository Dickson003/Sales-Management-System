package salesmanagement2;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class PasswordforgotController {

    @FXML
    private Button CANCEL;

      @FXML
    private Button back;
      
    @FXML
    private Button OK;

    @FXML
    private PasswordField password3;

    @FXML
    private PasswordField passwordc3;

    @FXML
    private TextField username;

    private DatabaseConnection databaseConnection = new DatabaseConnection(); // Initialize here

    @FXML
    void handleCANCELButtonAction(ActionEvent event) {
        // Clear all filled fields when the CANCEL button is clicked
        username.clear();
        password3.clear();
        passwordc3.clear();
    }

    @FXML
    void handleOKButtonAction(ActionEvent event) {
        if (validateFields()) {
            if (changePassword()) {
                // No need to load the Login scene here
            }
        }
    }

    private boolean validateFields() {
        if (username.getText().isEmpty() || password3.getText().isEmpty() || passwordc3.getText().isEmpty()) {
            showAlert("Error", "Please fill in all the fields.");
            return false;
        }

        // Validate if the new password matches the confirm password
        if (!password3.getText().equals(passwordc3.getText())) {
            showAlert("Error", "New password and confirm password do not match.");
            return false;
        }

        return true;
    }

    private boolean changePassword() {
        try {
            Connection connection = databaseConnection.getConnection();

            // Update the password
            String updateQuery = "UPDATE users SET password=? WHERE username=?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setString(1, password3.getText());
            updateStmt.setString(2, username.getText());

            int rowsAffected = updateStmt.executeUpdate();

            updateStmt.close();

            if (rowsAffected > 0) {
                showAlert("Success", "Password changed successfully!");
                closeWindow();
                return true;
            } else {
                showAlert("Error", "Failed to change password. Please try again.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage currentStage = (Stage) OK.getScene().getWindow();
        currentStage.close();
    }
    @FXML
    void handleBackAction(ActionEvent event) {
loadLoginScene();
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
