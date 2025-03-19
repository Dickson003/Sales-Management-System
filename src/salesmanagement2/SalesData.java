package salesmanagement2;

import javafx.beans.property.*;

public class SalesData {

    private final StringProperty transactionId;
    private final StringProperty invoiceNo;
    private final IntegerProperty productId;
    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final ObjectProperty<java.sql.Date> date;
    private final ObjectProperty<java.sql.Time> time;
    private final DoubleProperty totalAmount; // New property

    public SalesData(String transactionId, String invoiceNo, int productId, String productName, int quantity, java.sql.Date date, java.sql.Time time, double totalAmount) {
        this.transactionId = new SimpleStringProperty(transactionId);
        this.invoiceNo = new SimpleStringProperty(invoiceNo);
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.date = new SimpleObjectProperty<>(date);
        this.time = new SimpleObjectProperty<>(time);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
    }

    // Getter methods for each property
    public String getTransactionId() {
        return transactionId.get();
    }

    public String getInvoiceNo() {
        return invoiceNo.get();
    }

    public int getProductId() {
        return productId.get();
    }

    public String getProductName() {
        return productName.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public java.sql.Date getDate() {
        return date.get();
    }

    public java.sql.Time getTime() {
        return time.get();
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    // Property accessor methods
    public StringProperty transactionIdProperty() {
        return transactionId;
    }

    public StringProperty invoiceNoProperty() {
        return invoiceNo;
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public ObjectProperty<java.sql.Date> dateProperty() {
        return date;
    }

    public ObjectProperty<java.sql.Time> timeProperty() {
        return time;
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }
}
