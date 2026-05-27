

-- ============================================
-- CUSTOMERS TABLE
-- ============================================
-- Stores customer information
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

-- ============================================
-- ACCOUNTS TABLE
-- ============================================
-- Stores account information for customers
-- Can be WALLET or SAVINGS type
-- 
-- NEW FIELDS (Lab 2 Enhancements):
-- - balance_on_hold: Amount held for pending transfers
-- - failed_pin_attempts: Counter for failed PIN attempts
-- - account_status: ACTIVE, LOCKED, SUSPENDED, DORMANT, CLOSED
-- - locked_until: Timestamp when account will be unlocked
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    account_type VARCHAR(50) NOT NULL,  -- WALLET, SAVINGS
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    balance_on_hold DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    failed_pin_attempts INTEGER DEFAULT 0,
    account_status VARCHAR(20) DEFAULT 'ACTIVE',
    locked_until TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- ============================================
-- TRANSACTIONS TABLE
-- ============================================
-- Records all transaction activities
-- Types: DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN
-- 
-- NEW FIELDS (Lab 2 Enhancements):
-- - recipient_account_id: For transfers between accounts
-- - processed_at: When transaction was settled
-- - failure_reason: Why transaction failed
-- - transfer_fee: Fee applied for transfers
-- - reversed_by_transaction_id: Link to reversal transaction
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    reference_id VARCHAR(255) NOT NULL UNIQUE,
    transaction_type VARCHAR(50) NOT NULL,  -- DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- PENDING, SUCCESS, FAILED, CANCELLED
    description TEXT,
    recipient_account_id INTEGER,
    processed_at TIMESTAMP,
    failure_reason VARCHAR(500),
    transfer_fee DECIMAL(15, 2) DEFAULT 0,
    reversed_by_transaction_id INTEGER,
    rollback_reason VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    FOREIGN KEY (reversed_by_transaction_id) REFERENCES transactions(id) ON DELETE SET NULL
);

-- ============================================
-- PROCESSED_REQUESTS TABLE
-- ============================================
-- Tracks all processed transaction reference IDs
-- Prevents duplicate transactions from network retries
CREATE TABLE processed_requests (
    id SERIAL PRIMARY KEY,
    reference_id VARCHAR(255) NOT NULL UNIQUE,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- AUDIT_LOGS TABLE (NEW - Lab 2 Enhancements)
-- ============================================
-- Comprehensive audit trail for compliance
-- Records: PIN validations, status changes, transactions, locks/unlocks
CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    action VARCHAR(100) NOT NULL,  -- PIN_VALIDATION, TRANSACTION_DEPOSIT, ACCOUNT_LOCKED, etc.
    details TEXT,
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    status VARCHAR(20),  -- SUCCESS, FAILED, PENDING
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- ============================================
-- TRANSACTION_HOLDS TABLE (NEW - Lab 2 Phase 2)
-- ============================================
-- Tracks balance holds for pending transfers
-- Prevents double-spending during pending settlements
CREATE TABLE transaction_holds (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    reference_id VARCHAR(255) UNIQUE,
    hold_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    release_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, RELEASED, CANCELLED
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- ============================================
-- DAILY_BALANCE_SNAPSHOTS TABLE (NEW - Lab 2 Phase 3)
-- ============================================
-- Daily snapshots for reconciliation and audit
CREATE TABLE daily_balance_snapshots (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    balance_before DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    transaction_count INTEGER DEFAULT 0,
    total_debit DECIMAL(15, 2) DEFAULT 0,
    total_credit DECIMAL(15, 2) DEFAULT 0,
    snapshot_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reconciliation_status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, VERIFIED, DISCREPANCY
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- ============================================
-- INDEXES (for performance)
-- ============================================
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_accounts_account_status ON accounts(account_status);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_reference_id ON transactions(reference_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_processed_requests_reference_id ON processed_requests(reference_id);
CREATE INDEX idx_audit_logs_account_id ON audit_logs(account_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_transaction_holds_account_id ON transaction_holds(account_id);
CREATE INDEX idx_transaction_holds_reference_id ON transaction_holds(reference_id);
CREATE INDEX idx_balance_snapshots_account_date ON daily_balance_snapshots(account_id, snapshot_date);



