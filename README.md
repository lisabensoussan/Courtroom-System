# Courtroom Management System (HRS Project)

This Java project simulates an **Information Management System** for the newly founded **HRS Court** in Haifa.  
The system allows managing court cases, staff, clients, and hearings efficiently through an intuitive **GUI** and implements core **Object-Oriented Programming (OOP)** principles.

## 📋 Project Overview
- **Court Management**: Create, update, and delete court cases, clients, employees, and departments.
- **User Login System**: Different user roles (Admin, Judge, Lawyer, Staff) with specific permissions.
- **Queries and Statistics**: Advanced queries such as finding the busiest departments, assigning suitable lawyers, and analyzing case durations.
- **Serialization**: Save and reload the entire system state using a `Court.ser` file.
- **Error Handling**: Custom exception classes to manage invalid inputs and business logic errors.
- **GUI**: Built using **Swing** or **JavaFX** for a clean, intuitive, and user-friendly interface.

## 🛠 Tech Stack
- Java
- GUI
- Object-Oriented Programming (OOP)
- File I/O (CSV and Serializable files)

## 📦 Project Structure
```
src/
├── model/         # Core classes: Case, Client, Court, Department, etc.
├── control/       # Business logic: CourtController, etc.
├── view/          # User Interface: Main.java, Login Screen, Dashboards
├── utils/         # Utilities: CSV Readers, Log Writers
├── autopilot/     # File management automation
├── enums/         # Enumerations: Gender, Status, Specialization, Position
└── exceptions/    # Custom exceptions (e.g., NegativeSalaryException)
data/
├── *.csv          # Initial data for users, departments, cases
Court.ser          # Serialized file for saving system state
```

## 🚀 How to Run
1. Open the project in **Eclipse** or **IntelliJ IDEA**.
2. Run `Main.java` located in the `view` package.
3. Login with one of the default users:
   - **Admin**: Username `ADMIN`, Password `ADMIN`
   - Other users from the CSV file.
4. Explore court management operations!

## 🎯 Main Features
- **Admin Functions**: Add/remove judges, lawyers, and staff; manage court departments.
- **Judge Functions**: Manage assigned cases, schedule hearings, deliver verdicts.
- **Lawyer Functions**: Submit documents, view client files, appeal cases.
- **Staff Functions**: Manage departments, schedule hearings, assign rooms.
- **Advanced Queries**:
  - Find inactive cases by department.
  - Identify suitable lawyers based on case specialization.
  - Determine oldest employee for department management.
- **User Interface**:
  - Interactive menus.
  - Informative popups for success and error messages.
  - Public view of judgments (no login required).

## ⚙️ Deployment
- The project can be exported as a runnable `jar` file.
- Ensure the `Court.ser` file is present to load previous system states.

## 🧪 Learning Objectives
- Implement real-world **OOP** architecture.
- Create modular and scalable codebases.
- Build professional desktop **GUI** applications.
- Handle exceptions properly and ensure robust system behavior.
