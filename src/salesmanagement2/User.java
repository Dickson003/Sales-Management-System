package salesmanagement2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty account_type;
    private final StringProperty gender;
    private final StringProperty emailAddress;

    public User(String username, String password, String account_type, String gender, String emailAddress) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.account_type = new SimpleStringProperty(account_type);
        this.gender = new SimpleStringProperty(gender);
        this.emailAddress = new SimpleStringProperty(emailAddress);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String value) {
        username.set(value);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String value) {
        password.set(value);
    }

    public String getAccountType() {
        return account_type.get();
    }

    public void setAccountType(String value) {
        account_type.set(value);
    }

    public String getGender() {
        return gender.get();
    }

    public void setGender(String value) {
        gender.set(value);
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }

    public void setEmailAddress(String value) {
        emailAddress.set(value);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty accountTypeProperty() {
        return account_type;
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public StringProperty emailAddressProperty() {
        return emailAddress;
    }
}
