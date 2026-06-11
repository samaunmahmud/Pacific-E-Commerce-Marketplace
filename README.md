# Pacific E-Commerce Marketplace 🛒✨

A local JavaFX-based e-commerce marketplace application consisting of seven technically independent components. This project was developed as part of the NC1605 Group Project module at Brunel University London Pathway College.

The complete system architecture integrates shared logic, relational databases, and dynamic desktop interfaces built using Java, FXML, and SQLite.


## 🌟 My Component: Reviews & Ratings ⭐️💬

I was individually responsible for designing, implementing, and testing the **Reviews & Ratings** subsystem. This component enables registered customers to provide verified feedback on products and gives administrators tools to manage community content.

### 🚀 Key Features Implemented:

* Verified Customer Reviews:** Integrated a secure login restriction ensuring only registered users who have actually purchased a product can leave a feedback submission. 🔒🧾


* Time-Windowed Editing:** Customers can dynamically edit or delete their posted reviews within an adjustable timer window (e.g., 5 minutes). ⏳⏱️


* Content Filtering & Smart Sorting:** Beautiful interface views to display all reviews for a specific product, neatly sorted by date, rating, or helpfulness metrics. 📅📈


* Data Validation Guardrails:** Comprehensive validation scripts to block duplicate user reviews on the same product. Added security constraints protecting database text entry fields against unauthorized or strange characters `(%, # / \ ? " ' ~ $)`. 🛑🛡️


* Admin Moderation Portal:** An administrative view dashboard allowing managers to flag, edit, or remove reviews that violate community guidelines. 🛠️🖥️


* Dynamic Metrics Calculation:** Automated backend handling to compute and display real-time average star ratings (1–5 stars) per product. 📊🔢





## 🛠️ Tech Stack & Environment 💻⚙️

* **Language:** Java 21 ☕️
* **GUI Framework:** JavaFX 23 (utilizing FXML and custom CSS layouts) 🎨🖼️
* **Build System:** Apache Maven 📦🏗️
* **Database Engine:** SQLite JDBC Driver 🗄️💾
* **Testing Suite:** JUnit 🧪✅


## 🏎️ Getting Started

### 📋 Prerequisites

Make sure you have the Java Development Kit (JDK 21) installed on your system, and that your `JAVA_HOME` environment variable points to it. 🗺️🔧

### 📂 Opening the Project in VS Code

To ensure Maven resolves the plugin dependencies properly:

1. Open VS Code 💻
2. Select **File > Open Folder...** and pick the top-level root directory (`PACIFIC_E_COMMERCE_MARKPLACE`). Do not open individual source subdirectories! 📁❌
3. Wait for the Java Extension Pack status indicator to finish indexing your local configuration. ⏳👍

### 🏃‍♂️ Running the Application

From the root terminal directory (where the `pom.xml` file is located), execute the following command: 🔥👇

```bash
mvn clean javafx:run

```


## 🧪 Testing Layout 📋🔍

The validation suite balances automated structural checks and user-interface assertions:

* Unit Tests:** Handled with **JUnit** to verify correct logic, string matches, database saves, and rating boundaries. 🤖✅


* Manual Verification:** Comprehensive form boundary testing to guarantee illegal characters or duplicate inputs trigger clean user-facing error prompts. 👥⚠️




## 📁 File & Database Architecture Summary 🗺️🗂️

* `src/main/java/UITest1/UITest1/` — Core program classes including FXML Controllers and Application configurations. 🤖💻
* `src/main/resources/` — Layout definitions, image components, and graphic visuals. 🎨🌆
* `pom.xml` — Explicit dependency mappings for target Java compilers, module configurations, and operational configurations. ⚙️📐
* `DataBase1.db` — Relational SQLite target schema mapping `reviewID`, `productID`, `customerID`, `rating`, `comment`, and `timestamps` globally. 🗄️⚡️
