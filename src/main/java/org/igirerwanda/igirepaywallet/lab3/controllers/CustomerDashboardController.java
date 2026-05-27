package org.igirerwanda.igirepaywallet.lab3.controllers;

import java.io.IOException;

import org.igirerwanda.igirepaywallet.lab1.Account;
import org.igirerwanda.igirepaywallet.lab1.Transaction;
import org.igirerwanda.igirepaywallet.lab3.services.CustomerService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.print.PrinterJob;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class CustomerDashboardController {
    @FXML
    private Label customerNameLabel;
    @FXML
    private StackPane contentPane;
    @FXML
    private VBox dashboardView;
    @FXML
    private VBox accountsView;
    @FXML
    private VBox transactionsView;
    @FXML
    private VBox transferView;
    @FXML
    private VBox reportsView;
    @FXML
    private VBox accountsContainer;
    @FXML
    private ComboBox<String> fromAccountCombo;
    @FXML
    private ComboBox<String> txnAccountCombo;
    @FXML
    private TextField recipientAccountIdField;
    @FXML
    private ComboBox<String> reportAccountCombo;
    @FXML
    private TextField transferAmountField;
    @FXML
    private PasswordField transferPinField;
    @FXML
    private Label transferStatusLabel;
    @FXML
    private TextArea statementTextArea;
    @FXML
    private Label statusLabel;

    @FXML
    private TableView<?> accountsTable;
    @FXML
    private TableView<?> transactionsTable;
    @FXML
    private TableView<?> holdsTable;

    private CustomerService customerService;
    private Stage stage;
    private int customerId;
    private java.util.List<Account> cachedAccounts = java.util.Collections.emptyList();

    @FXML
    public void initialize() {
        customerService = new CustomerService();
        setupAccountsTableColumns();
        setupTransactionsTableColumns();
        showDashboard();
    }

    @SuppressWarnings("unchecked")
    private void setupAccountsTableColumns() {
        Object columns = accountsTable.getColumns();
        javafx.collections.ObservableList<?> cols = (javafx.collections.ObservableList<?>) columns;
        if (cols.size() >= 6) {
            javafx.scene.control.TableColumn<Account, Integer> col0 = (javafx.scene.control.TableColumn<Account, Integer>) cols.get(0);
            col0.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountId"));
            javafx.scene.control.TableColumn<Account, String> col1 = (javafx.scene.control.TableColumn<Account, String>) cols.get(1);
            col1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountType"));
            javafx.scene.control.TableColumn<Account, Double> col2 = (javafx.scene.control.TableColumn<Account, Double>) cols.get(2);
            col2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("balance"));

            javafx.scene.control.TableColumn<Account, Double> col3 = (javafx.scene.control.TableColumn<Account, Double>) cols.get(3);
            col3.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("balanceOnHold"));


            javafx.scene.control.TableColumn<Account, Double> col4 = (javafx.scene.control.TableColumn<Account, Double>) cols.get(4);
            col4.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(
                cell.getValue().getBalance() - cell.getValue().getBalanceOnHold()
            ));

            javafx.scene.control.TableColumn<Account, String> col5 = (javafx.scene.control.TableColumn<Account, String>) cols.get(5);
            col5.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("accountStatus"));
        }
    }

    @SuppressWarnings("unchecked")
    private void setupTransactionsTableColumns() {
        Object columns = transactionsTable.getColumns();
        javafx.collections.ObservableList<?> cols = (javafx.collections.ObservableList<?>) columns;
        if (cols.size() >= 6) {
            javafx.scene.control.TableColumn<Transaction, String> col0 = (javafx.scene.control.TableColumn<Transaction, String>) cols.get(0);
            col0.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("transactionId"));
            javafx.scene.control.TableColumn<Transaction, String> col1 = (javafx.scene.control.TableColumn<Transaction, String>) cols.get(1);
            col1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("transactionType"));
            javafx.scene.control.TableColumn<Transaction, Double> col2 = (javafx.scene.control.TableColumn<Transaction, Double>) cols.get(2);
            col2.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("amount"));

            javafx.scene.control.TableColumn<Transaction, String> col3 = (javafx.scene.control.TableColumn<Transaction, String>) cols.get(3);
            col3.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

            javafx.scene.control.TableColumn<Transaction, java.time.LocalDateTime> col4 =
                (javafx.scene.control.TableColumn<Transaction, java.time.LocalDateTime>) cols.get(4);
            col4.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("timestamp"));

            javafx.scene.control.TableColumn<Transaction, String> col5 = (javafx.scene.control.TableColumn<Transaction, String>) cols.get(5);
            col5.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        customerNameLabel.setText("Welcome, Customer #" + customerId);
        loadCustomerData();
    }

    @FXML
    private void showDashboard() {
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        transferView.setVisible(false);
        transferView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        loadDashboardContent();
    }

    @FXML
    private void showAccounts() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        accountsView.setVisible(true);
        accountsView.setManaged(true);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        transferView.setVisible(false);
        transferView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        statusLabel.setText("Loading accounts...");
        loadAccountsData();
    }

    @SuppressWarnings("unchecked")
    private void loadAccountsData() {
        try {
            java.util.List<Account> accounts = customerService.getCustomerAccounts(customerId);
            cachedAccounts = accounts != null ? accounts : java.util.Collections.emptyList();
            refreshAccountSelectors();
            if (accounts != null && !accounts.isEmpty()) {
                javafx.collections.ObservableList<Account> data = javafx.collections.FXCollections.observableArrayList(accounts);
                ((javafx.scene.control.TableView<Account>) accountsTable).setItems(data);
                statusLabel.setText("✓ Loaded " + accounts.size() + " accounts");
            } else {
                statusLabel.setText("No accounts found");
                ((javafx.scene.control.TableView<Account>) accountsTable).setItems(javafx.collections.FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading accounts: " + e.getMessage());
        }
    }

    @FXML
    private void showTransactions() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(true);
        transactionsView.setManaged(true);
        transferView.setVisible(false);
        transferView.setManaged(false);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        statusLabel.setText("Loading transactions...");
        loadTransactionsData();
    }

    @SuppressWarnings("unchecked")
    private void loadTransactionsData() {
        try {
            ensureAccountsLoaded();
            Account selected = getSelectedTxnAccountOrFirst();
            if (selected != null) {
                java.util.List<Transaction> allTransactions = customerService.getAccountTransactions(selected.getAccountId());
                javafx.collections.ObservableList<Transaction> data = javafx.collections.FXCollections.observableArrayList(allTransactions);
                ((javafx.scene.control.TableView<Transaction>) transactionsTable).setItems(data);
                statusLabel.setText("✓ Loaded " + (allTransactions != null ? allTransactions.size() : 0) + " transactions");
            } else {
                statusLabel.setText("No transactions found");
                ((javafx.scene.control.TableView<Transaction>) transactionsTable).setItems(javafx.collections.FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading transactions: " + e.getMessage());
        }
    }

    @FXML
    private void showTransfers() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        transferView.setVisible(true);
        transferView.setManaged(true);
        reportsView.setVisible(false);
        reportsView.setManaged(false);
        ensureAccountsLoaded();
        statusLabel.setText("Ready to transfer");
    }

    @FXML
    private void showReports() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        accountsView.setVisible(false);
        accountsView.setManaged(false);
        transactionsView.setVisible(false);
        transactionsView.setManaged(false);
        transferView.setVisible(false);
        transferView.setManaged(false);
        reportsView.setVisible(true);
        reportsView.setManaged(true);
        ensureAccountsLoaded();
        refreshAccountSelectors();
        statementTextArea.setText(customerService.getStatement(customerId));
        statusLabel.setText("✓ Statement loaded");
    }

    @FXML
    private void createDeposit() {
        ensureAccountsLoaded();
        Account selected = getSelectedTxnAccountOrFirst();
        if (selected == null) {
            showAlert("No accounts found for this customer.", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit Money");
        dialog.setHeaderText("Deposit to Account #" + selected.getAccountId());
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                TextInputDialog pinDialog = new TextInputDialog();
                pinDialog.setTitle("PIN Verification");
                pinDialog.setHeaderText("Enter your 4-digit PIN to confirm deposit");
                pinDialog.setContentText("PIN:");
                pinDialog.showAndWait().ifPresent(pin -> {
                    boolean ok = customerService.depositMoney(customerId, selected.getAccountId(), amount, pin.trim());
                    statusLabel.setText(ok ? "✓ Deposit successful" : "❌ Deposit failed");
                    loadAccountsData();
                    loadTransactionsData();
                    loadDashboardContent();
                });
            } catch (Exception ex) {
                showAlert("Invalid amount.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void createWithdraw() {
        ensureAccountsLoaded();
        Account selected = getSelectedTxnAccountOrFirst();
        if (selected == null) {
            showAlert("No accounts found for this customer.", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Withdraw Money");
        amountDialog.setHeaderText("Withdraw from Account #" + selected.getAccountId());
        amountDialog.setContentText("Amount:");

        amountDialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                TextInputDialog pinDialog = new TextInputDialog();
                pinDialog.setTitle("PIN Verification");
                pinDialog.setHeaderText("Enter your 4-digit PIN to confirm withdrawal");
                pinDialog.setContentText("PIN:");

                pinDialog.showAndWait().ifPresent(pin -> {
                    boolean ok = customerService.withdrawMoney(customerId, selected.getAccountId(), amount, pin.trim());
                    statusLabel.setText(ok ? "✓ Withdrawal successful" : "❌ Withdrawal failed");
                    loadAccountsData();
                    loadTransactionsData();
                    loadDashboardContent();
                });
            } catch (Exception ex) {
                showAlert("Invalid amount.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void initiateTransfer() {
        String fromAcc = fromAccountCombo.getValue();
        String toAcc = recipientAccountIdField != null ? recipientAccountIdField.getText().trim() : null;
        String amount = transferAmountField.getText().trim();
        String pin = transferPinField.getText().trim();

        if (fromAcc == null || toAcc == null || amount.isEmpty() || pin.isEmpty()) {
            transferStatusLabel.setText("❌ Please fill all fields");
            return;
        }

        boolean success = customerService.initiateTransfer(customerId, fromAcc, toAcc, 
            Double.parseDouble(amount), pin);
        if (success) {
            transferStatusLabel.setText("✅ Transfer successful.");
            clearTransferForm();
        } else {
            transferStatusLabel.setText("❌ Transfer failed. Check PIN and balance.");
        }
    }

    @FXML
    private void clearTransferForm() {
        if (recipientAccountIdField != null) {
            recipientAccountIdField.clear();
        }
        transferAmountField.clear();
        transferPinField.clear();
        transferStatusLabel.setText("");
    }

    @FXML
    private void exportStatement() {
        boolean success = customerService.exportStatement(customerId);
        if (success) {
            showAlert("✅ Statement exported to CSV", Alert.AlertType.INFORMATION);
            statusLabel.setText("✓ Export complete");
        } else {
            showAlert("❌ Error exporting statement", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void printStatement() {
        String statement = customerService.getStatement(customerId);
        statementTextArea.setText(statement);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert("❌ No printer available.", Alert.AlertType.ERROR);
            return;
        }
        boolean proceed = job.showPrintDialog(stage);
        if (!proceed) {
            return;
        }
        boolean ok = job.printPage(statementTextArea);
        if (ok) {
            job.endJob();
            showAlert("✅ Sent to printer.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("❌ Print failed.", Alert.AlertType.ERROR);
        }
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

    private void loadCustomerData() {
        ensureAccountsLoaded();
        loadDashboardContent();
        statusLabel.setText("✓ Data loaded");
    }

    private void loadDashboardContent() {
        ensureAccountsLoaded();
        accountsContainer.getChildren().clear();

        if (cachedAccounts.isEmpty()) {
            Label empty = new Label("No accounts found. Please ask admin to create an account for you.");
            empty.setStyle("-fx-padding: 15; -fx-border-color: #E8E8E8; -fx-font-size: 14; -fx-text-fill: #333;");
            accountsContainer.getChildren().add(empty);
            return;
        }

        for (Account acc : cachedAccounts) {
            String label = (acc.getAccountType().equalsIgnoreCase("WALLET") ? "💳" : "🏦") +
                " " + acc.getAccountType() +
                " (ID: " + acc.getAccountId() + ")" +
                " - Balance: RWF " + String.format("%,.2f", acc.getBalance()) +
                " | On Hold: RWF " + String.format("%,.2f", acc.getBalanceOnHold());
            Label card = new Label(label);
            card.setWrapText(true);
            card.setStyle("-fx-padding: 15; -fx-border-color: #E8E8E8; -fx-font-size: 14; -fx-text-fill: #333;");
            accountsContainer.getChildren().add(card);
        }
    }

    private void ensureAccountsLoaded() {
        if (cachedAccounts == null || cachedAccounts.isEmpty()) {
            java.util.List<Account> accounts = customerService.getCustomerAccounts(customerId);
            cachedAccounts = accounts != null ? accounts : java.util.Collections.emptyList();
            refreshAccountSelectors();
        }
    }

    private void refreshAccountSelectors() {
        if (fromAccountCombo == null || reportAccountCombo == null) {
            return;
        }
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (Account a : cachedAccounts) {
            ids.add(String.valueOf(a.getAccountId()));
        }
        fromAccountCombo.getItems().setAll(ids);
        reportAccountCombo.getItems().setAll(ids);

        if (txnAccountCombo != null) {
            txnAccountCombo.getItems().setAll(ids);
            if (txnAccountCombo.getValue() == null && !ids.isEmpty()) {
                txnAccountCombo.setValue(ids.get(0));
            }
        }
    }

    private Account getSelectedAccountOrFirst() {
        try {
            Account selected = ((javafx.scene.control.TableView<Account>) accountsTable).getSelectionModel().getSelectedItem();
            if (selected != null) return selected;
        } catch (Exception ignored) {}
        return cachedAccounts.isEmpty() ? null : cachedAccounts.get(0);
    }

    private Account getSelectedTxnAccountOrFirst() {
        ensureAccountsLoaded();
        if (txnAccountCombo != null && txnAccountCombo.getValue() != null) {
            try {
                int accountId = Integer.parseInt(txnAccountCombo.getValue().trim());
                for (Account a : cachedAccounts) {
                    if (a.getAccountId() == accountId) return a;
                }
            } catch (Exception ignored) {}
        }
        return getSelectedAccountOrFirst();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("IgirePay");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
