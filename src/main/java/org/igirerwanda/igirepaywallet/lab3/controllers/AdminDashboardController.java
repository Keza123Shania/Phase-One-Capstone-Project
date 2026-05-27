package org.igirerwanda.igirepaywallet.lab3.controllers;

import java.io.IOException;

import org.igirerwanda.igirepaywallet.lab1.Account;
import org.igirerwanda.igirepaywallet.lab1.Customer;
import org.igirerwanda.igirepaywallet.lab1.Transaction;
import org.igirerwanda.igirepaywallet.lab2.AuditLog;
import org.igirerwanda.igirepaywallet.lab3.services.AdminService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AdminDashboardController {
    @FXML
    private Label adminNameLabel;
    @FXML
    private StackPane contentPane;
    @FXML
    private VBox dashboardView;
    @FXML
    private VBox customersView;
    @FXML
    private VBox accountsView;
    @FXML
    private VBox transactionsView;
    @FXML
    private VBox auditView;
    @FXML
    private VBox reportsView;
    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label totalAccountsLabel;
    @FXML
    private Label activeTransactionsLabel;
    @FXML
    private Label systemStatusLabel;
    @FXML
    private Label lastSyncLabel;
    @FXML
    private Label statusLabel;

    @FXML
    private TextField newCustomerNameField;
    @FXML
    private TextField newCustomerEmailField;
    @FXML
    private TableView<?> customersTable;
    @FXML
    private TableView<?> accountsTable;
    @FXML
    private TableView<?> transactionsTable;
    @FXML
    private TableView<?> auditTable;

    private AdminService adminService;
    private Stage stage;

    @FXML
    public void initialize() {
        adminService = new AdminService();
        setupCustomersTableColumns();
        setupAccountsTableColumns();
        setupTransactionsTableColumns();
        setupAuditTableColumns();
        showDashboard();
        loadDashboardData();
    }

    @SuppressWarnings("unchecked")
    private void setupCustomersTableColumns() {
        Object columns = customersTable.getColumns();
        javafx.collections.ObservableList<?> cols = (javafx.collections.ObservableList<?>) columns;
        if (cols.size() >= 5) {
            javafx.scene.control.TableColumn<Customer, Integer> col0 = (javafx.scene.control.TableColumn<Customer, Integer>) cols.get(0);
            col0.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
            javafx.scene.control.TableColumn<Customer, String> col1 = (javafx.scene.control.TableColumn<Customer, String>) cols.get(1);
            col1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("fullName"));
            javafx.scene.control.TableColumn<Customer, String> col2 = (javafx.scene.control.TableColumn<Customer, String>) cols.get(2);
            col2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));


            javafx.scene.control.TableColumn<Customer, Object> col3 = (javafx.scene.control.TableColumn<Customer, Object>) cols.get(3);
            col3.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(""));


            javafx.scene.control.TableColumn<Customer, Object> actionsCol = (javafx.scene.control.TableColumn<Customer, Object>) cols.get(4);
            actionsCol.setCellFactory(tc -> new javafx.scene.control.TableCell<>() {
                private final javafx.scene.control.Button createAccountBtn = new javafx.scene.control.Button("Create Account");
                private final javafx.scene.control.Button editBtn = new javafx.scene.control.Button("Edit");
                private final javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("Delete");
                private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, createAccountBtn, editBtn, deleteBtn);

                {
                    createAccountBtn.getStyleClass().add("btn-primary");
                    editBtn.getStyleClass().add("btn-secondary");
                    deleteBtn.getStyleClass().add("btn-success");

                    createAccountBtn.setOnAction(e -> {
                        Customer c = getTableView().getItems().get(getIndex());
                        promptCreateAccount(c);
                    });
                    editBtn.setOnAction(e -> {
                        Customer c = getTableView().getItems().get(getIndex());
                        promptEditCustomer(c);
                    });
                    deleteBtn.setOnAction(e -> {
                        Customer c = getTableView().getItems().get(getIndex());
                        promptDeleteCustomer(c);
                    });
                }

                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void setupAccountsTableColumns() {
        Object columns = accountsTable.getColumns();
        javafx.collections.ObservableList<?> cols = (javafx.collections.ObservableList<?>) columns;
        if (cols.size() >= 6) {
            javafx.scene.control.TableColumn<Account, Integer> col0 = (javafx.scene.control.TableColumn<Account, Integer>) cols.get(0);
            col0.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountId"));

            javafx.scene.control.TableColumn<Account, Integer> col1 = (javafx.scene.control.TableColumn<Account, Integer>) cols.get(1);
            col1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("customerId"));

            javafx.scene.control.TableColumn<Account, String> col2 = (javafx.scene.control.TableColumn<Account, String>) cols.get(2);
            col2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountType"));

            javafx.scene.control.TableColumn<Account, Double> col3 = (javafx.scene.control.TableColumn<Account, Double>) cols.get(3);
            col3.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("balance"));

            javafx.scene.control.TableColumn<Account, String> col4 = (javafx.scene.control.TableColumn<Account, String>) cols.get(4);
            col4.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountStatus"));

            javafx.scene.control.TableColumn<Account, java.time.LocalDateTime> col5 =
                (javafx.scene.control.TableColumn<Account, java.time.LocalDateTime>) cols.get(5);
            col5.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("createdAt"));
        }
    }

    @SuppressWarnings("unchecked")
    private void setupTransactionsTableColumns() {
        Object columns = transactionsTable.getColumns();
        javafx.collections.ObservableList<?> cols = (javafx.collections.ObservableList<?>) columns;
        if (cols.size() >= 6) {
            javafx.scene.control.TableColumn<Transaction, Integer> col0 = (javafx.scene.control.TableColumn<Transaction, Integer>) cols.get(0);
            col0.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("transactionId"));

            javafx.scene.control.TableColumn<Transaction, Integer> col1 = (javafx.scene.control.TableColumn<Transaction, Integer>) cols.get(1);
            col1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountId"));

            javafx.scene.control.TableColumn<Transaction, String> col2 = (javafx.scene.control.TableColumn<Transaction, String>) cols.get(2);
            col2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("transactionType"));

            javafx.scene.control.TableColumn<Transaction, Double> col3 = (javafx.scene.control.TableColumn<Transaction, Double>) cols.get(3);
            col3.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("amount"));

            javafx.scene.control.TableColumn<Transaction, String> col4 = (javafx.scene.control.TableColumn<Transaction, String>) cols.get(4);
            col4.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

            javafx.scene.control.TableColumn<Transaction, java.time.LocalDateTime> col5 =
                (javafx.scene.control.TableColumn<Transaction, java.time.LocalDateTime>) cols.get(5);
            col5.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("timestamp"));
        }
    }

    @SuppressWarnings("unchecked")
    private void setupAuditTableColumns() {
        Object columns = auditTable.getColumns();
        javafx.collections.ObservableList<?> cols = (javafx.collections.ObservableList<?>) columns;
        if (cols.size() >= 5) {
            javafx.scene.control.TableColumn<AuditLog, java.time.LocalDateTime> col0 =
                (javafx.scene.control.TableColumn<AuditLog, java.time.LocalDateTime>) cols.get(0);
            col0.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("createdAt"));

            javafx.scene.control.TableColumn<AuditLog, Integer> col1 = (javafx.scene.control.TableColumn<AuditLog, Integer>) cols.get(1);
            col1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountId"));

            javafx.scene.control.TableColumn<AuditLog, String> col2 = (javafx.scene.control.TableColumn<AuditLog, String>) cols.get(2);
            col2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("action"));

            javafx.scene.control.TableColumn<AuditLog, String> col3 = (javafx.scene.control.TableColumn<AuditLog, String>) cols.get(3);
            col3.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("details"));

            javafx.scene.control.TableColumn<AuditLog, String> col4 = (javafx.scene.control.TableColumn<AuditLog, String>) cols.get(4);
            col4.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAdminName(String name) {
        adminNameLabel.setText("Welcome, " + name);
    }

    @FXML
    private void showDashboard() {
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        customersView.setVisible(false);
        customersView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        auditView.setVisible(false);
        auditView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        loadDashboardData();
    }

    @FXML
    private void showCustomers() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        customersView.setVisible(true);
        customersView.setManaged(true);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        auditView.setVisible(false);
        auditView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        statusLabel.setText("Loading customers...");
        loadCustomersData();
    }

    @SuppressWarnings("unchecked")
    private void loadCustomersData() {
        try {
            java.util.List<Customer> customers = adminService.getAllCustomers();
            if (customers != null && !customers.isEmpty()) {
                javafx.collections.ObservableList<Customer> data = javafx.collections.FXCollections.observableArrayList(customers);
                ((javafx.scene.control.TableView<Customer>) customersTable).setItems(data);
                statusLabel.setText("✓ Loaded " + customers.size() + " customers");
            } else {
                statusLabel.setText("No customers found");
                ((javafx.scene.control.TableView<Customer>) customersTable).setItems(javafx.collections.FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading customers: " + e.getMessage());
        }
    }

    @FXML
    private void showAccounts() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        customersView.setVisible(false);
        customersView.setManaged(false);
        accountsView.setVisible(true);
        accountsView.setManaged(true);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        auditView.setVisible(false);
        auditView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        statusLabel.setText("Loading accounts...");
        loadAccountsData();
    }

    @SuppressWarnings("unchecked")
    private void loadAccountsData() {
        try {
            java.util.List<Account> accounts = adminService.getAllAccounts();
            if (accounts != null && !accounts.isEmpty()) {
                javafx.collections.ObservableList<Account> data = javafx.collections.FXCollections.observableArrayList(accounts);
                ((javafx.scene.control.TableView<Account>) accountsTable).setItems(data);
                statusLabel.setText("✓ Loaded " + accounts.size() + " accounts");
            } else {
                ((javafx.scene.control.TableView<Account>) accountsTable).setItems(javafx.collections.FXCollections.observableArrayList());
                statusLabel.setText("No accounts found");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading accounts: " + e.getMessage());
        }
    }

    @FXML
    private void showTransactions() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        customersView.setVisible(false);
        customersView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(true);
        transactionsView.setManaged(true);
        auditView.setVisible(false);
        auditView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        statusLabel.setText("Loading transactions...");
        loadTransactionsData();
    }

    @SuppressWarnings("unchecked")
    private void loadTransactionsData() {
        try {
            java.util.List<Transaction> txns = adminService.getAllTransactions();
            if (txns != null && !txns.isEmpty()) {
                javafx.collections.ObservableList<Transaction> data = javafx.collections.FXCollections.observableArrayList(txns);
                ((javafx.scene.control.TableView<Transaction>) transactionsTable).setItems(data);
                statusLabel.setText("✓ Loaded " + txns.size() + " transactions");
            } else {
                ((javafx.scene.control.TableView<Transaction>) transactionsTable).setItems(javafx.collections.FXCollections.observableArrayList());
                statusLabel.setText("No transactions found");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading transactions: " + e.getMessage());
        }
    }

    @FXML
    private void showAuditTrail() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        customersView.setVisible(false);
        customersView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        auditView.setVisible(true);
        auditView.setManaged(true);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        statusLabel.setText("Loading audit trail...");
        loadAuditData();
    }

    @SuppressWarnings("unchecked")
    private void loadAuditData() {
        try {
            java.util.List<AuditLog> logs = adminService.getAuditLogs(200);
            if (logs != null && !logs.isEmpty()) {
                javafx.collections.ObservableList<AuditLog> data = javafx.collections.FXCollections.observableArrayList(logs);
                ((javafx.scene.control.TableView<AuditLog>) auditTable).setItems(data);
                statusLabel.setText("✓ Loaded " + logs.size() + " audit logs");
            } else {
                ((javafx.scene.control.TableView<AuditLog>) auditTable).setItems(javafx.collections.FXCollections.observableArrayList());
                statusLabel.setText("No audit logs found");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading audit logs: " + e.getMessage());
        }
    }

    @FXML
    private void showReports() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        customersView.setVisible(false);
        customersView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        auditView.setVisible(false);
        auditView.setManaged(false);
        reportsView.setVisible(true);
        reportsView.setManaged(true);
        statusLabel.setText("Ready to generate reports");
    }

    @FXML
    private void addCustomer() {
        String name = newCustomerNameField.getText().trim();
        String email = newCustomerEmailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Please fill all fields", Alert.AlertType.WARNING);
            return;
        }

        String credentials = adminService.addCustomer(name, email);
        if (credentials != null) {
            String[] parts = credentials.split(":");
            String customerId = parts[0];
            String pin = parts[1];
            

            String message = "Customer Created Successfully!\n\n" +
                             "Customer ID: " + customerId + "\n" +
                             "Default PIN: " + pin + "\n\n" +
                             "Share these credentials with the customer for login.";
            showAlert(message, Alert.AlertType.INFORMATION);
            
            newCustomerNameField.clear();
            newCustomerEmailField.clear();
            statusLabel.setText("✓ Customer created: ID " + customerId);
            

            loadCustomersData();
        } else {
            showAlert("❌ Error adding customer", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void exportTransactions() {
        boolean success = adminService.exportAllTransactions();
        if (success) {
            showAlert("✅ Transactions exported to CSV", Alert.AlertType.INFORMATION);
            statusLabel.setText("✓ Export complete");
        } else {
            showAlert("❌ Error exporting transactions", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void generateDailySummary() {
        String summary = adminService.generateDailySummary();
        showAlert(summary, Alert.AlertType.INFORMATION);
        statusLabel.setText("✓ Daily summary generated");
    }

    @FXML
    private void generateReconciliation() {
        String report = adminService.generateReconciliation();
        showAlert(report, Alert.AlertType.INFORMATION);
        statusLabel.setText("✓ Reconciliation generated");
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/igirerwanda/igirepaywallet/fxml/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/org/igirerwanda/igirepaywallet/css/styles.css").toExternalForm());

            LoginController controller = fxmlLoader.getController();
            controller.setStage(stage);

            stage.setScene(scene);
            stage.setTitle("IgirePay - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardData() {
        totalCustomersLabel.setText(String.valueOf(adminService.getTotalCustomers()));
        totalAccountsLabel.setText(String.valueOf(adminService.getTotalAccounts()));
        activeTransactionsLabel.setText(String.valueOf(adminService.getActiveTransactions()));
        lastSyncLabel.setText("Just now");
    }

    private void promptCreateAccount(Customer customer) {
        if (customer == null) return;

        javafx.scene.control.ChoiceDialog<String> typeDialog =
                new javafx.scene.control.ChoiceDialog<>("WALLET", java.util.List.of("WALLET", "SAVINGS"));
        typeDialog.setTitle("Create Account");
        typeDialog.setHeaderText("Create an account for " + customer.getFullName() + " (ID: " + customer.getId() + ")");
        typeDialog.setContentText("Account type:");

        typeDialog.showAndWait().ifPresent(type -> {
            TextInputDialog balDialog = new TextInputDialog("0");
            balDialog.setTitle("Initial Balance");
            balDialog.setHeaderText("Initial balance for " + type + " account");
            balDialog.setContentText("Amount:");

            balDialog.showAndWait().ifPresent(balanceStr -> {
                try {
                    double initialBalance = Double.parseDouble(balanceStr.trim());
                    int accountId = adminService.createAccountForCustomer(customer.getId(), type, initialBalance);
                    if (accountId > 0) {
                        showAlert("✅ Account created.\n\nCustomer ID: " + customer.getId() +
                                "\nAccount ID: " + accountId + "\nType: " + type, Alert.AlertType.INFORMATION);
                        loadDashboardData();
                    } else {
                        showAlert("❌ Failed to create account.", Alert.AlertType.ERROR);
                    }
                } catch (Exception ex) {
                    showAlert("❌ Invalid amount.", Alert.AlertType.ERROR);
                }
            });
        });
    }

    private void promptEditCustomer(Customer customer) {
        if (customer == null) return;

        TextInputDialog nameDialog = new TextInputDialog(customer.getFullName());
        nameDialog.setTitle("Edit Customer");
        nameDialog.setHeaderText("Edit customer (ID: " + customer.getId() + ")");
        nameDialog.setContentText("Full name:");

        nameDialog.showAndWait().ifPresent(newName -> {
            TextInputDialog emailDialog = new TextInputDialog(customer.getEmail());
            emailDialog.setTitle("Edit Customer");
            emailDialog.setHeaderText("Edit customer (ID: " + customer.getId() + ")");
            emailDialog.setContentText("Email:");

            emailDialog.showAndWait().ifPresent(newEmail -> {
                TextInputDialog phoneDialog = new TextInputDialog(customer.getPhoneNumber());
                phoneDialog.setTitle("Edit Customer");
                phoneDialog.setHeaderText("Edit customer (ID: " + customer.getId() + ")");
                phoneDialog.setContentText("Phone number:");

                phoneDialog.showAndWait().ifPresent(newPhone -> {
                    boolean ok = adminService.updateCustomer(customer.getId(), newName, newEmail, newPhone);
                    if (ok) {
                        statusLabel.setText("✓ Customer updated: ID " + customer.getId());
                        loadCustomersData();
                        loadDashboardData();
                    } else {
                        showAlert("❌ Failed to update customer.", Alert.AlertType.ERROR);
                    }
                });
            });
        });
    }

    private void promptDeleteCustomer(Customer customer) {
        if (customer == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Customer");
        confirm.setHeaderText("Delete customer " + customer.getFullName() + " (ID: " + customer.getId() + ")?");
        confirm.setContentText("This will remove the customer from the database. Depending on your DB constraints, their accounts/transactions may also be deleted.");
        java.util.Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            boolean ok = adminService.deleteCustomer(customer.getId());
            if (ok) {
                statusLabel.setText("✓ Customer deleted: ID " + customer.getId());
                loadCustomersData();
                loadDashboardData();
            } else {
                showAlert("❌ Failed to delete customer.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("IgirePay");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
