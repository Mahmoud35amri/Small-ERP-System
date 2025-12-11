# 🏢 Mini-ERP System

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-Modern_UI-02569B?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

> A professional, modular desktop ERP solution built with pure JavaFX, featuring rigorous multi-tenancy and a clean programmatic UI architecture.

---

## 📖 Overview

**Mini-ERP** is a robust desktop application designed to manage core business processes. Unlike traditional FXML-based JavaFX apps, this project utilizes a **Programmatic UI** approach for maximum type safety, performance, and maintainability.

It features a strict **Multi-Tenancy** architecture, ensuring data isolation between different enterprises, making it suitable for SaaS-style deployment scenarios or managing multiple business entities within a single installation.

## 🚀 Key Features

### 🏛️ Core Architecture
-   **Strict Multi-Tenancy**: Complete isolation of data (Clients, Stocks, Orders) per Enterprise.
-   **Role-Based Access Control (RBAC)**: Secure hierarchy with `ADMIN`, `GERANT` (Manager), `VENDEUR` (Sales), and `STOCK_MANAGER` roles.
-   **Session Management**: Singleton-based session handling with secure logout.

### 📦 Inventory & Stock
-   **Real-time Tracking**: Live monitoring of stock levels across categories.
-   **Movement History**: Detailed audit trail of all `ENTREE` and `SORTIE` stock movements.
-   **Category Management**: Hierarchical organization of products.

### 💼 Commercial (CRM & Sales)
-   **Client Management**: Comprehensive customer database.
-   **Supplier Relations**: Streamlined supplier management with a simple 0-5 **Evaluation** system.
-   **Order Processing**: End-to-end lifecycle from draft to validated order.
-   **Invoicing**: One-click generation of invoices from validated orders.

### 📊 Analytics
-   **Interactive Dashboard**: Dynamic charts visualizing sales trends and stock distribution.
-   **KPI Cards**: Instant view of total revenue, active orders, and client count.

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 21 | Leveraging records, pattern matching, and new APIs. |
| **UI Framework** | JavaFX 21 | Modern desktop toolkit. |
| **Components** | ControlsFX | Advanced controls (Notifications, Validation). |
| **Build Tool** | Maven | Standard dependency management. |
| **Data** | In-Memory | Runs entirely in memory (with seed data) for instant setup. |

## 🏁 Getting Started

### Prerequisites
*   **JDK 21** or higher installed.
*   **Maven 3.6+** installed.

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/minierp.git
    cd minierp
    ```

2.  **Build the project**
    ```bash
    mvn clean install
    ```

3.  **Run the application**
    ```bash
    mvn javafx:run
    ```

## 🔐 Default Credentials

The application comes pre-seeded with demo data. Use these accounts to explore different roles:

| Role | Username (Email) | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| 👑 **ADMIN** | `admin@minierp.com` | `admin123` | **Full System Access** (All enterprises) |
| 👔 **GERANT** | `gerant@minierp.com` | `gerant123` | **Enterprise Manager** (Staff & Ops) |
| 🛒 **VENDEUR** | `vendeur@minierp.com` | `vendeur123` | **Sales Only** (Orders, Clients) |

> **Note**: Data is reset every time you restart the application (In-Memory storage).

## 🏗️ Project Structure

```text
src/main/java/com/minierp
├── controller/    # Business Logic & Singletons
├── model/         # Domain Entities (Data Classes)
├── service/       # Auth & Session Services
├── ui/            # Pure JavaFX Views (No FXML)
│   ├── components/# Reusable UI Widgets
│   └── [module]/  # Feature-specific Views
└── util/          # Helpers (Seeding, Formatters)
```

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---
*Built with ❤️ by the Mini-ERP Team.*
