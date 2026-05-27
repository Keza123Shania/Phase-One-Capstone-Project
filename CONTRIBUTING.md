# Contributing to IgirePay

## 🌳 Git Workflow

This project follows a feature branch workflow. All development happens on feature branches; changes are merged to `main` via pull requests.

### Branch Naming Convention

- `feature/lab1-*` - Lab 1 features (OOP Design)
- `feature/lab2-*` - Lab 2 features (JDBC & DAO)
- `feature/lab3-*` - Lab 3 features (Integration & Reports)
- `bugfix/*` - Bug fixes
- `docs/*` - Documentation updates

Example: `feature/lab3-transaction-reports`, `bugfix/pin-validation`, `docs/readme-update`

## 🚀 Creating a Feature Branch

### 1. Start from main
```bash
git checkout main
git pull origin main
```

### 2. Create your feature branch
```bash
git checkout -b feature/your-feature-name
```

### 3. Make your changes
- Write clean, readable code
- Add comments for complex logic
- Follow Java naming conventions
- One feature per branch

### 4. Commit with clear messages
```bash
git add .
git commit -m "Add feature: descriptive message"
```

**Commit message format:**
```
[COMPONENT] Brief description

- Detailed explanation if needed
- Additional context
```

**Examples:**
```
[Lab3] Implement PIN-based authentication
[Lab3] Add transaction report generation to CSV
[Lab2] Fix connection pooling memory leak
[Docs] Update setup instructions for PostgreSQL
```

### 5. Push to remote
```bash
git push origin feature/your-feature-name
```

### 6. Create a Pull Request
- Go to GitHub: https://github.com/Keza123Shania/Phase-One-Capstone-Project
- Click "New Pull Request"
- Select `main` as base, your branch as compare
- Add description of what changed and why
- Request review

### 7. Address Review Comments
```bash
# Make requested changes
git add .
git commit -m "Address review: [specific change]"
git push origin feature/your-feature-name
```

### 8. Merge to Main
Once approved:
```bash
git checkout main
git pull origin main
git merge feature/your-feature-name
git push origin main
```

### 9. Delete feature branch
```bash
git branch -d feature/your-feature-name
git push origin --delete feature/your-feature-name
```

## 📋 Commit Message Best Practices

### Good ✅
```
[Lab3] Implement account locking after failed PIN attempts

- Add failed_attempts counter to Account class
- Lock account after 3 failed PIN attempts
- Add unlockAccount() method
- Update PIN validation to check lock status
```

### Bad ❌
```
fixed stuff
updated code
changes
```

## 🔀 Handling Merge Conflicts

If conflicts arise during pull requests:

```bash
# Pull latest main
git fetch origin
git rebase origin/main

# Resolve conflicts in your IDE
# Then continue rebase
git add .
git rebase --continue

# Push updated branch
git push origin feature/your-feature-name -f
```

## 📝 Code Style

- **Naming:** camelCase for variables/methods, PascalCase for classes
- **Formatting:** 4-space indentation
- **Comments:** JavaDoc for public methods, inline for complex logic
- **Exception Handling:** Use custom exceptions when appropriate
- **PreparedStatements:** ALWAYS use for SQL queries (never string concatenation)

## ✅ Before Pushing

1. **Compile successfully**
   ```bash
   mvn clean compile
   ```

2. **Run existing tests**
   ```bash
   mvn test
   ```

3. **Follow code style**
   - Check for unused imports
   - Consistent indentation
   - Proper variable naming

4. **Meaningful commits**
   - One logical change per commit
   - Clear commit messages
   - Test commits before pushing

## 🎯 Exercise Branches

### Lab 1 Development
```bash
git checkout -b feature/lab1-oop-design
# Implement: Account, WalletAccount, SavingsAccount, etc.
```

### Lab 2 Development
```bash
git checkout -b feature/lab2-jdbc-dao
# Implement: DAOs, Database schema, JDBC connection
```

### Lab 3 Development
```bash
git checkout -b feature/lab3-integration
# Implement: Integrated console, exception handling

git checkout -b feature/lab3-authentication
# Implement: PIN locking, account status

git checkout -b feature/lab3-reports
# Implement: CSV export, transaction summaries
```

## 🏷️ Tagging Releases

After merging a lab completion:

```bash
git tag -a v1.0-lab1 -m "Lab 1: OOP Design Complete"
git tag -a v2.0-lab2 -m "Lab 2: JDBC & DAO Pattern Complete"
git tag -a v3.0-lab3 -m "Lab 3: Integrated Payment App Complete"

git push origin --tags
```

## 📊 Viewing Branch History

```bash
# View all branches
git branch -a

# View branch history
git log --graph --decorate --oneline

# View changes in branch
git log main..feature/your-feature-name

# Compare branches
git diff main..feature/your-feature-name
```

## 🔄 Pulling Latest Changes

```bash
# Update main from remote
git checkout main
git pull origin main

# Rebase your feature branch on latest main
git checkout feature/your-feature-name
git rebase main
```

## ❓ Troubleshooting

### Oops, I committed to main instead of a branch
```bash
git reset --soft HEAD~1          # Undo last commit
git checkout -b feature/new-branch
git commit -m "proper message"
```

### I need to discard all local changes
```bash
git reset --hard origin/main
```

### Accidentally deleted a branch?
```bash
git reflog                       # Find the commit
git checkout -b recovered-branch <commit-hash>
```

---

**Questions?** Reach out to the team or check GitHub Issues.

Happy coding! 🚀
