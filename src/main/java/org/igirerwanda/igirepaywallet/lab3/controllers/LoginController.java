package org.igirerwanda.igirepaywallet.lab3.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.igirerwanda.igirepaywallet.lab3.services.AuthService;

import java.io.IOException;


public class LoginController {
    @FXML
    private Button adminRoleButton;
    @FXML
    private Button customerRoleButton;
    @FXML
    private VBox adminLoginBox;
    @FXML
    private VBox customerLoginBox;
    @FXML
    private TextField adminUsernameField;
    @FXML
    private PasswordField adminPasswordField;
    @FXML
    private TextField customerIdField;
    @FXML
    private PasswordField pinField;
    @FXML
    private Button adminLoginButton;
    @FXML
    private Button customerLoginButton;
    @FXML
    private Label errorLabel;

    private AuthService authService;
    private Stage stage;

    @FXML
    public void initialize() {
        authService = new AuthService();
        setCustomerRole();  // Default to customer role
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void setAdminRole() {
        adminLoginBox.setVisible(true);
        adminLoginBox.setManaged(true);
        customerLoginBox.setVisible(false);
        customerLoginBox.setManaged(false);

        adminRoleButton.setStyle("-fx-background-color: #FD6F2F;");
        customerRoleButton.setStyle("-fx-background-color: #DCE4B8;");
        errorLabel.setText("");
    }

    @FXML
    private void setCustomerRole() {
        adminLoginBox.setVisible(false);
        adminLoginBox.setManaged(false);
        customerLoginBox.setVisible(true);
        customerLoginBox.setManaged(true);

        customerRoleButton.setStyle("-fx-background-color: #FD6F2F;");
        adminRoleButton.setStyle("-fx-background-color: #DCE4B8;");
        errorLabel.setText("");
    }

    @FXML
    private void loginAsAdmin() {
        String username = adminUsernameField.getText().trim();
        String password = adminPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("❌ Please enter both username and password");
            return;
        }


        if (authService.authenticateAdmin(username, password)) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/igirerwanda/igirepaywallet/fxml/admin-dashboard.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
                scene.getStylesheets().add(getClass().getResource("/org/igirerwanda/igirepaywallet/css/styles.css").toExternalForm());

                AdminDashboardController controller = fxmlLoader.getController();
                controller.setStage(stage);
                controller.setAdminName(username);

                stage.setScene(scene);
                stage.setTitle("IgirePay - Admin Dashboard");
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("❌ Error loading admin dashboard");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("❌ Invalid admin credentials");
            adminPasswordField.clear();
        }
    }

    @FXML
    private void loginAsCustomer() {
        String customerId = customerIdField.getText().trim();
        String pin = pinField.getText().trim();

        if (customerId.isEmpty() || pin.isEmpty()) {
            errorLabel.setText("❌ Please enter both Customer ID and PIN");
            return;
        }

        if (authService.authenticateCustomer(customerId, pin)) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/igirerwanda/igirepaywallet/fxml/customer-dashboard.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
                scene.getStylesheets().add(getClass().getResource("/org/igirerwanda/igirepaywallet/css/styles.css").toExternalForm());

                CustomerDashboardController controller = fxmlLoader.getController();
                controller.setStage(stage);
                controller.setCustomerId(Integer.parseInt(customerId));

                stage.setScene(scene);
                stage.setTitle("IgirePay - My Wallet");
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("❌ Error loading customer dashboard");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("❌ Invalid Customer ID or PIN");
            pinField.clear();
        }
    }
}
