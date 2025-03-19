package salesmanagement2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.Optional;

public class AdminInterfaceController {

    @FXML
    private Button exit;

    @FXML
    private Button inventory;

    @FXML
    private Button profiles;

    @FXML
    private Button sales;

    @FXML
    void handleExitAction(ActionEvent event) {
        // Show a confirmation dialog before exiting
        showExitConfirmationDialog();
    }

    @FXML
    void handleInventoryAction(ActionEvent event) {
        // Add logic for handling the inventory action
        loadInventoryScene();
    }

    @FXML
    void handleProfilesAction(ActionEvent event) {
        // Load UserProfiles.fxml when the "Profiles" button is clicked
        loadUserProfileScene();
    }

    @FXML
    void handleSalesAction(ActionEvent event) {
        // Add logic for handling the sales action
        loadViewSalesScene();
    }

    private void showExitConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Admin's Dashboard");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to leave Admin's Dashboard?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user clicks OK, close the stage
            Stage stage = (Stage) exit.getScene().getWindow();
            stage.close();
        }
    }

    private void loadInventoryScene() {
        try {
             Stage currentStage = (Stage) inventory.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }
    }

    private void loadUserProfileScene() {
        try {
             Stage currentStage = (Stage) profiles.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfiles.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }
    }
  
       private void loadViewSalesScene() {
    try {
        // Obtain the reference to the current stage
        Stage currentStage = (Stage) sales.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewSales.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

        // Close the current stage (window)
        currentStage.close();
    } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception appropriately (e.g., show an error message)
    }
}

}
