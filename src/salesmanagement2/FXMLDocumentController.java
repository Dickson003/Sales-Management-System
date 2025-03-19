package salesmanagement2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FXMLDocumentController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private Stage stage;

    private DatabaseConnection databaseConnection; // Add a reference to the DatabaseConnection

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void handleCreateAccountButtonAction(ActionEvent event) {
        closeCurrentStage();
        loadScene("Register.fxml");
    }

    @FXML
    void handleForgotPasswordButtonAction(ActionEvent event) {
        closeCurrentStage();
        loadScene("Passwordforgot.fxml");
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showAlert("Error", "Error loading scene.");
        }
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        try {
            String inputUsername = username.getText();
            String inputPassword = password.getText();

            Connection connection = databaseConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE BINARY username = ? AND password = ?");
            pstmt.setString(1, inputUsername);
            pstmt.setString(2, inputPassword);

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                String userType = resultSet.getString("account_type");
                System.out.println("User Type: " + userType); // Add this line for debugging
                showAlert("Login Successful", "Welcome, " + inputUsername + "!");

                if ("ADMIN".equals(userType)) {
                    closeCurrentStage();
                    loadScene("AdminInterface.fxml");
                } else if ("USER".equals(userType)) {
                    closeCurrentStage();
                    loadScene("CustomersInterface.fxml");
                } else {
                    showAlert("Error", "Unknown user type: " + userType);
                }
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }

            // Close resources
            resultSet.close();
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            showAlert("Error", "An error occurred during login.");
            e.printStackTrace();
        }
    }

    private void closeCurrentStage() {
        Stage currentStage = (Stage) username.getScene().getWindow();
        currentStage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        username.clear();
        password.clear();
    }

    // Initialize the DatabaseConnection
    public void initialize() {
        databaseConnection = new DatabaseConnection();
    }
}
