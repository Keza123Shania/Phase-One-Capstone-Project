

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
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    account_type VARCHAR(50) NOT NULL,  -- WALLET, SAVINGS
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- ============================================
-- TRANSACTIONS TABLE
-- ============================================
-- Records all transaction activities
-- Types: DEPOSIT, WITHDRAW, TRANSFER
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    reference_id VARCHAR(255) NOT NULL UNIQUE,
    transaction_type VARCHAR(50) NOT NULL,  -- DEPOSIT, WITHDRAW, TRANSFER
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- PENDING, SUCCESS, FAILED
    description TEXT,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
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
-- INDEXES (for performance)
-- ============================================
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_reference_id ON transactions(reference_id);
CREATE INDEX idx_processed_requests_reference_id ON processed_requests(reference_id);


