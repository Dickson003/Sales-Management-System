package salesmanagement2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ViewSalesController {

    @FXML
    private Button Back;
    
    @FXML
    private TableColumn<SalesData, java.sql.Date> Date;

    @FXML
    private TableColumn<SalesData, String> InvoiceNoColumn;

    @FXML
    private TableColumn<SalesData, Integer> ProductIdColumn;

    @FXML
    private TableColumn<SalesData, String> ProductName;

    @FXML
    private TableColumn<SalesData, Integer> Quantity;

    @FXML
    private TableView<SalesData> SalesTable;

    @FXML
    private TableColumn<SalesData, java.sql.Time> Time;

    @FXML
    private TableColumn<SalesData, Double> TotalAmount;

    @FXML
    private TableColumn<SalesData, String> TransIdColumn;
    
     private final DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    public void initialize() {
        // Initialize the columns with the correct data type
        Date.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        InvoiceNoColumn.setCellValueFactory(cellData -> cellData.getValue().invoiceNoProperty());
        ProductIdColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
        ProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        Quantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        Time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        TotalAmount.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty().asObject());
        TransIdColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty());
        
        // Fetch data from the database and populate the TableView
        fetchDataFromDatabase();
    } 
    private void fetchDataFromDatabase() {
        try {
            Connection connection = databaseConnection.getConnection();

           String query = "SELECT * FROM sales ORDER BY Date DESC, Time DESC"; // Order by Date and Time in descending order
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<SalesData> salesDataList = new ArrayList<>();

            while (resultSet.next()) {
                SalesData salesData = new SalesData(
                        resultSet.getString("Transaction_id"),
                        resultSet.getString("Invoice_no"),
                        resultSet.getInt("Product_id"),
                        resultSet.getString("Product_Name"),
                        resultSet.getInt("Quantity"),
                        resultSet.getDate("Date"),
                        resultSet.getTime("Time"),
                        resultSet.getDouble("Total_Amount")
                );
                salesDataList.add(salesData);
            }

            resultSet.close();
            preparedStatement.close();

            ObservableList<SalesData> observableSalesDataList = FXCollections.observableArrayList(salesDataList);
            SalesTable.setItems(observableSalesDataList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleBackAction(ActionEvent event) {
  // Handle navigation back to the AdminInterface.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Back.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
