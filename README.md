# Food Ordering System

A desktop Food Ordering & Restaurant Management application developed in Java Swing with MySQL for database management.

---

## Features

- **User Authentication**: Secure user registration and login using PBKDF2 with HMAC-SHA-256 and cryptographic salts.
- **Restaurant & Menu Browsing**: Browse restaurants, cuisines, categorized food items, and live ratings.
- **Cart & Order Processing**: Multi-item cart management with atomic transactional order placement (`commit` / `rollback`).
- **Interactive Swing UI**: Custom styled Swing interface with custom rounded panels, buttons, and theme constants.

---

## Prerequisites

- **Java Development Kit (JDK)**: JDK 17 or higher (tested with modern OpenJDK/Oracle JDK).
- **MySQL Server**: MySQL 8.0 or higher.
- **MySQL Connector/J**: JDBC driver included in `lib/` (`mysql-connector-j-26.7.0.jar`).

---

## Database Setup

> [!CAUTION]
> **Destructive Database Operation**: `database/schema.sql` contains `DROP TABLE IF EXISTS` statements. Running this script will drop existing tables and data for this database schema. Ensure you backup any existing data before executing it.

1. **Start your MySQL server**:
   Ensure MySQL service is active and listening on port `3306`.

2. **Run schema and seed scripts**:
   Open a terminal, MySQL Workbench, or your preferred SQL client and execute:
   ```sql
   -- Create tables and views (WARNING: Drops existing tables if present)
   SOURCE database/schema.sql;

   -- Populate initial categories, restaurants, and menu items
   SOURCE database/seed_data.sql;
   ```

3. **(Recommended) Create a dedicated MySQL User (Least Privilege)**:
   Avoid running desktop applications under the MySQL `root` account. Create a dedicated user with access limited to `food_ordering_system`:
   ```sql
   CREATE USER 'food_app_user'@'localhost' IDENTIFIED BY 'StrongPasswordHere123!';
   GRANT SELECT, INSERT, UPDATE, DELETE ON food_ordering_system.* TO 'food_app_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

---

## Configuration (`db.properties`)

The application loads connection parameters from a local file named `db.properties` at runtime. For security reasons, this file is excluded by `.gitignore` and must never be committed to public version control.

1. Create `db.properties` in the project root by copying the template:
   ```bash
   # Windows PowerShell
   Copy-Item db.properties.example db.properties

   # Linux / macOS
   cp db.properties.example db.properties
   ```

2. Open `db.properties` and enter your database credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/food_ordering_system
   db.user=food_app_user
   db.password=StrongPasswordHere123!
   ```

---

## Building and Running

### Compile
From the root directory of the project:

```powershell
# Create output directory
mkdir out

# Compile all source files
javac -cp "lib/*" -d out (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
```

### Run
```powershell
java -cp "out;lib/*" Main
```
*(On Linux/macOS, replace `;` with `:` in classpath: `java -cp "out:lib/*" Main`)*

---

## Project Structure

```
FoodOrderingSystem/
├── .vscode/               # VS Code workspace settings (referenced libraries)
├── database/              # Database scripts and diagrams
│   ├── schema.sql         # Table schemas and views (contains DROP TABLE)
│   ├── seed_data.sql      # Initial menu and restaurant records
│   ├── demo_queries.sql   # Verification and testing SQL queries
│   └── erd.mwb            # MySQL Workbench ERD model
├── images/                # Application image assets
├── lib/                   # Third-party runtime libraries (MySQL JDBC driver)
├── src/                   # Java application source code
│   ├── dao/               # Data Access Objects (UserDAO, MenuDAO, OrderDAO, RestaurantDAO)
│   ├── database/          # Database connection provider (DBConnection)
│   ├── gui/               # Swing JFrame windows (Login, Register, Home, Cart, Menu, Receipt)
│   ├── model/             # Domain entity models (User, MenuItem, Order, Restaurant, CartItem)
│   ├── ui/                # Swing custom UI components and styling
│   └── util/              # Utilities (Session, CartManager, WindowHelper)
├── .gitignore             # Git exclusion rules
├── db.properties.example  # Configuration template (safe for version control)
├── Report.docx            # Project documentation report
└── README.md              # Project setup and security guide
```

---

## Security Notes

- **Excluded Secrets**: `db.properties` is protected via `.gitignore`.
- **Password Security**: Passwords are never stored in plaintext. They are salted with 16 random bytes and hashed using PBKDF2WithHmacSHA256 (120,000 iterations). Hashes and salts are discarded from in-memory session objects after authentication.
- **SQL Injection Defense**: All user inputs are parameterized via `PreparedStatement`.
