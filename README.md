# Mini-ERP JavaFX Application

A pure JavaFX desktop application for Mini-ERP management, featuring a modular architecture, programmatic UI (no FXML), and ControlsFX integration.

## Features
- **Login Module**: Secure authentication with role-based access.
- **Entreprise Module**: CRUD operations for managing enterprises with in-table editing and search.
- **Sidebar Navigation**: Modern collapsible sidebar.
- **In-Memory Data**: Runs completely without a database (seeded with test data).

## Prerequisites
- Java 17 or higher
- Maven 3.6+

## How to Run
1.  Open a terminal in the project root.
2.  Run the following command:
    ```bash
    mvn clean javafx:run
    ```

## Test Credentials
The application is pre-seeded with the following users:

| Role | Email | Password |
|------|-------|----------|
| **ADMIN** | `admin@minierp.com` | `admin123` |
| **GERANT** | `gerant@minierp.com` | `gerant123` |

## Project Structure
- `com.minierp.model`: Data models (Utilisateur, Entreprise).
- `com.minierp.controller`: Business logic and data management.
- `com.minierp.ui`: UI components and ViewManager.
- `com.minierp.service`: Session management.

## Architecture
- **ViewManager**: Handles navigation and view switching.
- **Programmatic UI**: All views are built in Java code for type safety and performance.
- **Singleton Controllers**: Centralized data access.

## Future Modules
- Dashboard
- Client / Fournisseur
- Produit / Stock
- Commande / Facture
