# 🖥️ Online Examination System

An **Offline/Online Examination System** built in **Java (Swing)** designed to manage and conduct exams digitally.  
This project is ideal for schools, colleges, and training centers to streamline the examination process without manual effort.

---

## 📌 Features

- **User Login System** – Secure login for students.
- **Subject Selection** – Choose from multiple subjects before starting the exam.
- **Shuffled Questions** – Random question order to prevent cheating.
- **Navigation** – `Next` and `Previous` buttons to switch between questions.
- **Per-Question Timer** – Automatically moves to the next question after time ends.
- **Instant Result Generation** – Shows total score and correct/incorrect answers.
- **No Database Needed** – Data stored in code for easy execution and testing.

---

## 🛠️ Technologies Used

- **Java** – Core programming language
- **Swing (JFrame, JPanel, JButton, JLabel)** – GUI components
- **Collections Framework** – For storing and shuffling questions
- **AWT** – Event handling and UI layout

---

## 📂 Project Structure

```mermaid
graph TD
    A[OnlineExaminationSystem] --> B[src]
    B --> B1[LoginPage.java<br/>User login interface]
    B --> B2[ExamPage.java<br/>Question navigation and timer]
    B --> B3[ResultPage.java<br/>Displays score]
    A --> C[README.md<br/>Project documentation]
    A --> D[LICENSE<br/>License file (if applicable)]
```

---

## 🚀 Getting Started

### 1️⃣ Prerequisites
- **Java JDK 8+**
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans) or terminal with `javac` & `java`.

### 2️⃣ Installation & Run
```bash
# Clone the repository
git clone https://github.com/varaprasad946/OnlineExaminationSystem.git

# Open in IDE or compile via terminal
javac src/*.java

# Run the main class
java src.OfflineExamApp
```
