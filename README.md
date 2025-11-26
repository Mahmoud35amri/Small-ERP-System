# Mini-ERP JavaFX Application

A comprehensive, pure JavaFX desktop application for Mini-ERP management. This application features a modular architecture, programmatic UI (no FXML), and a robust multi-tenancy system.

## Features

### 🏢 Core & Multi-Tenancy
- **Multi-Tenancy**: Complete data isolation between enterprises. Users only see data relevant to their assigned enterprise.
- **Entreprise Management**: Create and manage multiple enterprises with specific configurations.
- **Role-Based Access Control (RBAC)**: Secure authentication with roles (ADMIN, GERANT, VENDEUR, STOCK_MANAGER).

### 📦 Inventory Management
- **Products & Categories**: Manage product catalog with categorization.
- **Stock Control**: Real-time stock tracking and manual adjustments.
- **Stock Movements**: History of all stock entries and exits.

### 🤝 CRM (Customer Relationship Management)
- **Clients**: Manage customer database with contact details.
- **Suppliers (Fournisseurs)****: Manage supplier relationships.

### 💰 Sales & Purchasing
- **Orders (Commandes)**: Create and track customer orders.
- **Invoices (Factures)**: Generate invoices from orders and track payments.

### 📊 Dashboard & Reporting
- **Visual Analytics**: Interactive charts for sales, stock levels, and revenue.
- **Key Metrics**: Real-time KPIs for quick business insights.

## Tech Stack
- **Java 21**: Leveraging the latest Java features.
- **JavaFX**: For a responsive and modern desktop UI.
- **ControlsFX**: Enhanced UI controls.
- **Maven**: Dependency management and build automation.
- **In-Memory Storage**: Runs completely without a database (seeded with test data) for easy testing and demonstration.

## Prerequisites
- Java 21 or higher
- Maven 3.6+

## How to Run
1.  Open a terminal in the project root.
2.  Run the following command:
    ```bash
    mvn clean javafx:run
    ```

## Test Credentials
The application is pre-seeded with test data. You can log in with the following users:

| Role | Email | Password | Context |
|------|-------|----------|---------|
| **ADMIN** | `admin@minierp.com` | `admin123` | Full Access |
| **GERANT** | `gerant@minierp.com` | `gerant123` | Management |
| **VENDEUR** | `vendeur@minierp.com` | `vendeur123` | Sales Only |

## Project Structure
- `com.minierp.model`: Domain entities (Utilisateur, Entreprise, Produit, etc.).
- `com.minierp.controller`: Business logic and singleton controllers.
- `com.minierp.ui`: UI components, views, and ViewManager.
- `com.minierp.service`: Session and authentication services.
- `com.minierp.util`: Utilities for dialogs, ID generation, and data seeding.

## Architecture
- **Programmatic UI**: All views are built in pure Java code for type safety, performance, and easier refactoring.
- **Singleton Pattern**: Controllers and Services use the singleton pattern for centralized state management.
- **Observer Pattern**: UI components update automatically upon data changes (where implemented).
