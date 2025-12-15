# 🏢 Mini-ERP System (v2.0 - AI Integrated)

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-Modern_UI-02569B?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Gemini](https://img.shields.io/badge/AI-Gemini_Flash-4285F4?style=for-the-badge&logo=google-gemini&logoColor=white)](https://deepmind.google/technologies/gemini/)

> A professional, modular desktop ERP solution built with pure JavaFX, featuring rigorous multi-tenancy, a clean programmatic UI architecture, and a powerful **Generative AI Assistant**.

---

## 📖 Overview

**Mini-ERP** is a robust desktop application designed to manage core business processes. Unlike traditional FXML-based JavaFX apps, this project utilizes a **Programmatic UI** approach for maximum type safety, performance, and maintainability.

It features a strict **Multi-Tenancy** architecture, ensuring data isolation between different enterprises, making it suitable for SaaS-style deployment scenarios or managing multiple business entities within a single installation.

**New in v2.0**: The system is now fully **localized in French** and includes an integrated **AI Assistant** capable of executing ERP actions via natural language commands.

## 🚀 Key Features

### 🤖 AI-Powered Assistant (New!)
-   **Natural Language Interface**: Speak to your ERP in English or French (e.g., "Montre les ventes d'aujourd'hui", "Create order for Client X").
-   **Intelligent Actions**: The AI can query data (Sales, Stock), create orders, and generate invoices autonomously.
-   **Secure Execution**: Intent is parsed into structured JSON actions and executed safely via verified DAOs—the AI never touches the database directly.
-   **Powered by Google Gemini 2.5 Flash**.

### 🇫🇷 Full Localization
-   **100% French UI**: All interfaces, buttons, notifications, and error messages are native French.

### 🏛️ Core Architecture
-   **Strict Multi-Tenancy**: Complete isolation of data (Clients, Stocks, Orders) per Enterprise.
-   **Role-Based Access Control (RBAC)**: Secure hierarchy with `ADMIN`, `GERANT` (Manager), `VENDEUR` (Sales), and `STOCK_MANAGER` roles.

### 📦 Inventory & Stock
-   **Real-time Tracking**: Live monitoring of stock levels across categories.
-   **Movement History**: Detailed audit trail of all `ENTREE` and `SORTIE` stock movements.
-   **Category Management**: Hierarchical organization of products.

### 💼 Commercial (CRM & Sales)
-   **Client Management**: Comprehensive customer database.
-   **Order Processing**: End-to-end lifecycle from draft to validated order to delivery.
-   **Invoicing**: One-click generation of PDF invoices from validated orders.

### 📊 Analytics
-   **Interactive Dashboard**: Dynamic charts visualizing sales trends and product distribution per category.

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 25 | Leveraging records, pattern matching, and new APIs. |
| **UI Framework** | JavaFX 21 | Modern desktop toolkit. |
| **AI Model** | Gemini 2.5 Flash | Fast, cost-effective LLM for query understanding. |
| **Build Tool** | Maven | Standard dependency management. |
| **Data** | In-Memory | Runs entirely in memory (with Tunisian seed data) for instant setup. |

## 🏁 Getting Started

### Prerequisites
*   **JDK 21** or higher.
*   **Maven 3.6+**.
*   (Optional) **Gemini API Key** for AI features.

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

## 🔐 Default Credentials (Tunisian Data Demo)

The application comes pre-seeded with recent Tunisian business data.

| Role | Username (Email) | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| 👑 **ADMIN (Tunisie Élec)** | `mohamed@techexpert.tn` | `123456` | **Tech Enterprise** |
| 👑 **ADMIN (Sfax Agro)** | `fatma@sfaxagro.tn` | `123456` | **Agro Enterprise** |

> **Note**: Data is reset every time you restart the application.

## 🏗️ Project Structure

```text
src/main/java/com/minierp
├── ai/            # AI Module (Service, LLM Provider, Executors)
├── controller/    # Business Logic & Singletons
├── dao/           # Data Access Layer
├── model/         # Domain Entities (Data Classes)
├── service/       # Auth & Session Services
├── ui/            # JavaFX Views (Login, Dashboard, AI View)
└── util/          # Helpers (Seed Data, PDF Gen)
```
