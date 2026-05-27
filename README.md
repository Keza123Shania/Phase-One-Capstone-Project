# IgirePay Payment Gateway Project

**Building Secure Data-Driven JavaFX Applications with JDBC & OOP**

A comprehensive backend capstone project for a secure desktop-based digital wallet system inspired by MTN Mobile Money.

## 📋 Project Overview

IgirePay Technologies Ltd. is building a fintech solution that allows users to:
- Create and manage digital wallets and savings accounts
- Perform secure transactions (deposit, withdraw, transfer)
- Prevent duplicate transactions using idempotency
- Manage transaction history and generate reports
- Authenticate with PIN-based security

## 🎯 Learning Objectives

This project demonstrates:
- **Object-Oriented Programming** (Exercise 1.1-1.3)
- **Database Integration with JDBC** (Exercise 2.1-2.5)
- **Console Application Development** (Exercise 3.1)
- **Exception Handling & Error Recovery** (Exercise 3.2)
- **Transaction Reporting** (Exercise 3.3)
- **Authentication & Security** (Exercise 3.4)
- **Git/GitHub Collaboration Workflows** (Exercise 3.5)

## 🏗️ Project Structure

```
igirepay-wallet/
├── src/
│   ├── main/
│   │   ├── java/org/igirerwanda/igirepaywallet/
│   │   │   ├── HelloApplication.java          (Main entry point)
│   │   │   ├── HelloController.java
│   │   │   ├── Launcher.java
│   │   │   ├── lab1/                          (OOP Design)
│   │   │   │   ├── Account.java               (Abstract base class)
│   │   │   │   ├── WalletAccount.java         (Polymorphism)
│   │   │   │   ├── SavingsAccount.java        (Polymorphism)
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── DuplicateTransactionException.java
│   │   │   │   ├── IdempotencyManager.java
│   │   │   │   └── Lab1Console.java
│   │   │   └── lab2/                          (JDBC & DAO Pattern)
│   │   │       ├── DatabaseConnection.java
│   │   │       ├── CustomerDAO.java
│   │   │       ├── AccountDAO.java
│   │   │       ├── TransactionDAO.java
│   │   │       ├── ProcessedRequestDAO.java
│   │   │       └── Lab2Console.java
│   │   └── resources/
│   │       └── database_schema.sql
│   └── test/
└── pom.xml
```

## 🛠️ Technologies

| Component | Technology |
|-----------|-----------|
| Language | Java 11 |
| Build | Maven |
| Database | PostgreSQL |
| JDBC | org.postgresql:postgresql:42.7.1 |
| UI | JavaFX |
| Version Control | Git & GitHub |

## 💾 Database Schema

Four tables implement the payment system:

### customers
```sql
id (PK) | full_name | email | phone_number
```

### accounts
```sql
id (PK) | customer_id (FK) | account_type | balance | created_at | is_active
```

### transactions
```sql
id (PK) | account_id (FK) | reference_id (UNIQUE) | transaction_type | amount | created_at | status | description
```

### processed_requests
```sql
id (PK) | reference_id (UNIQUE) | processed_at
```

## 🚀 Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+
- PostgreSQL 12+
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Keza123Shania/Phase-One-Capstone-Project.git
cd igirepay-wallet
```

2. **Set up PostgreSQL Database**
```bash
# Create database
createdb -U postgres igirepay

# Run schema script
psql -U postgres -d igirepay -f src/main/resources/database_schema.sql
```

3. **Update Database Credentials**
Edit `src/main/java/org/igirerwanda/igirepaywallet/lab2/DatabaseConnection.java`:
```java
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "your_password";
```

4. **Build the Project**
```bash
mvn clean compile
```

5. **Run the Application**
```bash
mvn exec:java -Dexec.mainClass="org.igirerwanda.igirepaywallet.HelloApplication"
```

## 📚 Lab Breakdown

### Lab 1: Object-Oriented Design
**Focus:** Inheritance, Polymorphism, Collections

- **Account** (abstract): Base class for all account types
- **WalletAccount**: Standard wallet with instant transfers (0% fee)
- **SavingsAccount**: Savings account with 1% withdrawal fee, 10,000 RWF minimum
- **Idempotency Manager**: Detects duplicate transactions using HashSet
- **Lab1Console**: Interactive testing without dummy data

✅ **Completed Exercises:**
- 1.1: OOP class design with proper encapsulation
- 1.2: Polymorphic withdraw/deposit/processTransaction methods
- 1.3: Collections (List, Map, Set) for account & transaction management

### Lab 2: Database Integration with JDBC
**Focus:** DAO Pattern, PreparedStatements, SQL Injection Prevention

- **DatabaseConnection**: Centralized JDBC connection management
- **CustomerDAO**: CRUD for customers
- **AccountDAO**: CRUD for accounts with balance updates
- **TransactionDAO**: CRUD for transactions with reference tracking
- **ProcessedRequestDAO**: Idempotency at database level
- **Lab2Console**: Interactive database testing menu

✅ **Completed Exercises:**
- 2.1: PostgreSQL schema with 4 tables + indexes
- 2.2: Full CRUD operations (create, read, update, delete)
- 2.3: PreparedStatements prevent SQL injection
- 2.4: Four DAO classes with custom queries
- 2.5: Reference ID tracking prevents duplicate processing

### Lab 3: Mini Capstone - Integrated Payment App (IN PROGRESS)
**Focus:** Integration, Exception Handling, Reports, Authentication

🚧 **Planned Exercises:**
- 3.1: Menu-driven console combining Labs 1 & 2
- 3.2: Exception handling for 6+ error scenarios
- 3.3: Transaction reports (CSV export, daily summaries, statements)
- 3.4: PIN-based authentication with account locking
- 3.5: Git/GitHub workflows with feature branches

## 🔐 Security Features

### Current
- ✅ PreparedStatements for SQL injection prevention
- ✅ Idempotency protection against duplicate transactions
- ✅ PIN validation for account access

### Future
- 🔄 PIN hashing with bcrypt
- 🔄 Account locking after failed PIN attempts
- 🔄 Audit logging for compliance
- 🔄 Transaction status workflow
- 🔄 Balance holds for pending transfers

## 📊 Key Features

### Duplicate Transaction Prevention
Every transaction has a unique `reference_id`. The system:
1. Checks if reference_id exists in processed_requests table
2. Rejects duplicate requests immediately
3. Stores successful reference IDs for audit trail

### Account Types
| Feature | Wallet | Savings |
|---------|--------|---------|
| Withdrawal Fee | 0% | 1% |
| Minimum Balance | 0 RWF | 10,000 RWF |
| Use Case | Quick transfers | Long-term saving |

### Menu System
```
==== IGIREPAY WALLET SYSTEM ====
1. Lab 1: OOP Design (Interactive Testing)
2. Lab 2: Database Integration (JDBC Testing)
3. Lab 3: Integrated Payment App (IN PROGRESS)
4. GUI Mode (Coming Soon)
0. Exit
```

## 🌳 Git Workflow

This project uses feature branches for organized development:

```
main
├── feature/lab1-oop-design
├── feature/lab2-jdbc-dao
├── feature/lab3-integration
└── feature/lab3-advanced-logic
```

### Branch Strategy
- `main`: Production-ready code (release branch)
- `develop`: Integration branch for features
- `feature/*`: Individual feature branches
- Tag releases with `v1.0`, `v2.0`, etc.

### Making a Feature Branch
```bash
git checkout -b feature/your-feature-name
# Make changes
git add .
git commit -m "descriptive message"
git push origin feature/your-feature-name
```

## 📝 Exercises & Rubric

| Exercise | Status | Points |
|----------|--------|--------|
| 1.1 OOP Design | ✅ Complete | 20 |
| 1.2 Polymorphism | ✅ Complete | 20 |
| 1.3 Collections | ✅ Complete | 15 |
| 2.1 Database Schema | ✅ Complete | 15 |
| 2.2 CRUD Operations | ✅ Complete | 15 |
| 2.3 PreparedStatements | ✅ Complete | 10 |
| 2.4 DAO Pattern | ✅ Complete | 15 |
| 2.5 Idempotency | ✅ Complete | 10 |
| 3.1 Menu-Driven App | 🚧 In Progress | 15 |
| 3.2 Exception Handling | 🚧 In Progress | 15 |
| 3.3 Reports | 🚧 In Progress | 10 |
| 3.4 Authentication | 🚧 In Progress | 10 |
| 3.5 Git/GitHub | 🚧 In Progress | 10 |
| **TOTAL** | | **185** |

## 🧪 Testing

### Manual Testing
Run Lab1Console or Lab2Console to interactively test features without dummy data.

### Planned Automated Tests
- Unit tests for Account classes (withdraw/deposit logic)
- Integration tests for DAO operations
- End-to-end tests for payment workflows

## 📖 Code Quality

- Clean, straightforward, easygoing code
- No external libraries beyond Java standard library + PostgreSQL driver
- Proper exception handling
- Comprehensive JavaDoc comments
- Single entry point (HelloApplication.main())

## 🐛 Known Issues & TODOs

- [ ] PIN hashing (currently stored plain text)
- [ ] Account locking after failed PIN attempts
- [ ] Transaction status workflow (PENDING → SUCCESS/FAILED)
- [ ] Balance holds for pending transfers
- [ ] Audit trail logging
- [ ] JDBC transaction rollback
- [ ] CSV report generation
- [ ] Connection pooling
- [ ] Performance optimization

## 👥 Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for development guidelines and feature branch workflow.

## 📄 License

This project is part of a capstone program by IgiRE Rwanda and SheCanCode.

## 📞 Contact

**IgirePay Technologies Ltd.**
- Email: info@igirerwanda.org
- Mobile: +250 788 473 533
- Location: Kacyiru, KG 549 St, 36 | Gasabo, Kigali, Rwanda

---

**Built with ❤️ for secure fintech solutions**
