package salesmanagement2;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserProfilesController {

    @FXML
    private ComboBox<String> account_type;

    @FXML
    private Button add;

    @FXML
    private Button cancel;

    @FXML
    private TableColumn<User, String> columnEmailAddress;

    @FXML
    private TableColumn<User, String> columnGender;

    @FXML
    private TableColumn<User, String> columnaccount_type;

    @FXML
    private TableColumn<User, String> columnpassword;

    @FXML
    private TableColumn<User, String> columnusername;

    @FXML
    private TextField email;

    @FXML
    private RadioButton female;

    @FXML
    private ToggleGroup genderToggleGroup;

    @FXML
    private Button home;

    @FXML
    private RadioButton male;

    @FXML
    private PasswordField password;

    @FXML
    private Button remove;

    @FXML
    private Button update;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TextField username;

    @FXML
    void handleAccount_typeAction(ActionEvent event) {

    }

@FXML
void handleAddAction(ActionEvent event) {
    // Get values from the input fields
    String newUsername = username.getText();
    String newPassword = password.getText();
    String newAccountType = account_type.getValue();
    String newGender = (female.isSelected()) ? "Female" : "Male";
    String newEmail = email.getText();

    // Validate input
    if (newUsername.isEmpty() || newPassword.isEmpty() || newAccountType == null || newGender.isEmpty() || newEmail.isEmpty()) {
        showAlert("Incomplete Information", "Please fill in all fields.");
        return;
    }

    // Check if the username already exists
    if (isUsernameExists(newUsername)) {
        showAlert("Username Exists", "Username already exists. Please choose a different username.");
        return;
    }

    // Confirm user addition with an alert
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm User Addition");
    confirmAlert.setHeaderText("Are you sure you want to add this user?");
    confirmAlert.setContentText("Username: " + newUsername + "\nAccount Type: " + newAccountType);
    confirmAlert.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            // Proceed with adding the user
            User newUser = new User(newUsername, newPassword, newAccountType, newGender, newEmail);
            addUserToDatabase(newUser);
            userTable.getItems().add(newUser);
            clearFields();
        } else {
            // User canceled the addition, do nothing
        }
    });
}

    // Add this method to check if the username already exists
    private boolean isUsernameExists(String username) {
        ObservableList<User> usersList = userTable.getItems();

        for (User user : usersList) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void handleCancelAction(ActionEvent event) {
        // Clear all filled fields
        clearFields();
    }

    @FXML
    void handleHomeAction(ActionEvent event) {
        // Implement logic for navigating back to the Admin Interface
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminInterface.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) home.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
 }
@FXML
void handleRemoveAction(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
        // Confirm user deletion with an alert
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm User Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this user?");
        confirmAlert.setContentText("Username: " + selectedUser.getUsername() + "\nAccount Type: " + selectedUser.getAccountType());
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Proceed with removing the user
                boolean deletionSuccessful = removeUserFromDatabase(selectedUser);
                if (deletionSuccessful) {
                    userTable.getItems().remove(selectedUser);
                    clearFields();
                }
            } else {
                // User canceled the deletion, do nothing
            }
        });
    } else {
        showAlert("No User Selected", "Please select a user to remove.");
    }
}
@FXML
void handleUpdateAction(ActionEvent event) {
    // Get the selected user from the TableView
    User selectedUser = userTable.getSelectionModel().getSelectedItem();

    if (selectedUser != null) {
        // Update the selected user with the new values from the input fields
        updateSelectedUser(selectedUser);

        // Update the selected user in the database
        boolean updateSuccessful = updateUserInDatabase(selectedUser);

        if (updateSuccessful) {
            // If the update operation was successful, update the TableView
            updateInTableView(selectedUser);

            // Clear the input fields
            clearFields();
        } else {
            showAlert("Update Failed", "Failed to update the user.");
        }
    } else {
        showAlert("No User Selected", "Please select a user to update.");
    }
}

  @FXML
    void handleTableClick(MouseEvent event) {
        // Get the selected user from the TableView
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Fill the input fields with the selected user's information
            fillFields(selectedUser);
        }
    }

    private void fillFields(User user) {
        username.setText(user.getUsername());
        password.setText(user.getPassword());
        account_type.setValue(user.getAccountType());
        if ("Female".equals(user.getGender())) {
            female.setSelected(true);
        } else {
            male.setSelected(true);
        }
        email.setText(user.getEmailAddress());
    }

    @FXML
    void initialize() {
        // Initialize the TableView columns with the corresponding User fields
        columnusername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        columnaccount_type.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnEmailAddress.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));

        // Enable editing for each column
        enableEditingForColumns();

        // Set up the account_type ComboBox with predefined options
        ObservableList<String> accountTypes = FXCollections.observableArrayList("ADMIN", "USER");
        account_type.setItems(accountTypes);

        // Fetch and display users' data in the TableView during initialization
        populateUserTable();
    }

    private void enableEditingForColumns() {
        columnusername.setCellFactory(TextFieldTableCell.forTableColumn());
        columnusername.setOnEditCommit(this::handleEditCommitUsername);

        columnpassword.setCellFactory(TextFieldTableCell.forTableColumn());
        columnpassword.setOnEditCommit(this::handleEditCommitPassword);

        columnaccount_type.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaccount_type.setOnEditCommit(this::handleEditCommitAccountType);

        columnGender.setCellFactory(TextFieldTableCell.forTableColumn());
        columnGender.setOnEditCommit(this::handleEditCommitGender);

        columnEmailAddress.setCellFactory(TextFieldTableCell.forTableColumn());
        columnEmailAddress.setOnEditCommit(this::handleEditCommitEmailAddress);
    }

    private void handleEditCommitUsername(TableColumn.CellEditEvent<User, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setUsername(event.getNewValue());
        updateUserInDatabase(event.getRowValue());
        updateInTableView(event.getRowValue());
    }

    private void handleEditCommitPassword(TableColumn.CellEditEvent<User, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setPassword(event.getNewValue());
        updateUserInDatabase(event.getRowValue());
        updateInTableView(event.getRowValue());
    }

    private void handleEditCommitAccountType(TableColumn.CellEditEvent<User, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setAccountType(event.getNewValue());
        updateUserInDatabase(event.getRowValue());
        updateInTableView(event.getRowValue());
    }

    private void handleEditCommitGender(TableColumn.CellEditEvent<User, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setGender(event.getNewValue());
        updateUserInDatabase(event.getRowValue());
        updateInTableView(event.getRowValue());
    }

    private void handleEditCommitEmailAddress(TableColumn.CellEditEvent<User, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setEmailAddress(event.getNewValue());
        updateUserInDatabase(event.getRowValue());
        updateInTableView(event.getRowValue());
    }

    private void updateInTableView(User updatedUser) {
        ObservableList<User> items = userTable.getItems();

        int index = items.indexOf(updatedUser);
        if (index != -1) {
            items.set(index, updatedUser);
        } else {
            items.add(updatedUser);
        }
    }

    private void updateSelectedUser(User selectedUser) {
        selectedUser.setUsername(username.getText());
        selectedUser.setPassword(password.getText());
        selectedUser.setAccountType(account_type.getValue());
        selectedUser.setGender((female.isSelected()) ? "Female" : "Male");
        selectedUser.setEmailAddress(email.getText());
    }

private boolean updateUserInDatabase(User user) {
    try {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        String updateQuery = "UPDATE users SET password=?, account_type=?, Gender=?, EmailAddress=? WHERE username=?";
        PreparedStatement pstmt = connection.prepareStatement(updateQuery);
        pstmt.setString(1, user.getPassword());
        pstmt.setString(2, user.getAccountType());
        pstmt.setString(3, user.getGender());
        pstmt.setString(4, user.getEmailAddress());
        pstmt.setString(5, user.getUsername());

        int rowsUpdated = pstmt.executeUpdate();
        pstmt.close();
        connection.close();

        if (rowsUpdated > 0) {
            showAlert("Update Successful", "User updated successfully.");
            return true; // Indicate successful update
        } else {
            return false; // Indicate failed update
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Indicate failed update
    }
}
 private boolean removeUserFromDatabase(User user) {
    boolean deletionSuccessful = false;
    try {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        String deleteQuery = "DELETE FROM users WHERE username=?";
        PreparedStatement pstmt = connection.prepareStatement(deleteQuery);
        pstmt.setString(1, user.getUsername());

        int rowsDeleted = pstmt.executeUpdate();
        if (rowsDeleted > 0) {
            showAlert("Deletion Successful", "User deleted successfully.");
            deletionSuccessful = true;
        } else {
            showAlert("Deletion Failed", "Failed to delete the user.");
        }

        pstmt.close();
        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return deletionSuccessful;
}

    private void addUserToDatabase(User user) {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String insertQuery = "INSERT INTO users (username, password, account_type, Gender, EmailAddress) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getAccountType());
            pstmt.setString(4, user.getGender());
            pstmt.setString(5, user.getEmailAddress());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Addition Successful", "User added successfully.");
            } else {
                showAlert("Addition Failed", "Failed to add the user.");
            }

            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        username.clear();
        password.clear();
        account_type.getSelectionModel().clearSelection();
        genderToggleGroup.selectToggle(null);  // Clear the gender selection
        email.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void populateUserTable() {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            ObservableList<User> usersList = FXCollections.observableArrayList();
            String selectQuery = "SELECT * FROM users";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String accountType = resultSet.getString("account_type");
                String gender = resultSet.getString("Gender");
                String emailAddress = resultSet.getString("EmailAddress");

                usersList.add(new User(username, password, accountType, gender, emailAddress));
            }

            userTable.setItems(usersList);

            pstmt.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
