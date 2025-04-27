package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import control.Court;
import enums.*;
import model.*;
import utils.UtilsMethods;

public class EmployeeManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField idField, firstNameField, lastNameField, addressField, phoneField, emailField;
    private JComboBox<Gender> genderComboBox;
    private JComboBox<Position> positionComboBox;
    private JTextField salaryField;
    private JFormattedTextField birthDateField, workStartDateField;
    private JComboBox<Department> departmentComboBox;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Photo related fields
    private JLabel photoLabel;
    private byte[] selectedPhoto;
    private final int PHOTO_WIDTH = 150;
    private final int PHOTO_HEIGHT = 150;
    private JButton selectPhotoButton;

    /**
     * Create the frame for employee management.
     */
    public EmployeeManagementPanel() {
        court = Main.court;
        
        setTitle("Employee Management");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 900, 700);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create top panel that will contain both photo and form panels
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Create photo panel
        JPanel photoPanel = new JPanel(new BorderLayout(5, 5));
        photoPanel.setBorder(new TitledBorder("Profile Photo"));
        
        // Photo display
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setText("No Photo Selected");
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        
        // Photo selection button
        selectPhotoButton = new JButton("Select Photo");
        selectPhotoButton.addActionListener(e -> selectPhoto());
        photoPanel.add(selectPhotoButton, BorderLayout.SOUTH);
        
        // Form Panel for data entry
        JPanel formPanel = new JPanel();
        formPanel.setBorder(new TitledBorder(null, "Employee Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        formPanel.setLayout(new GridLayout(0, 4, 10, 10));
        
        // ID
        JLabel lblId = new JLabel("ID:");
        formPanel.add(lblId);
        
        idField = new JTextField();
        formPanel.add(idField);
        idField.setColumns(10);
        
        // First Name
        JLabel lblFirstName = new JLabel("First Name:");
        formPanel.add(lblFirstName);
        
        firstNameField = new JTextField();
        formPanel.add(firstNameField);
        firstNameField.setColumns(10);
        
        // Last Name
        JLabel lblLastName = new JLabel("Last Name:");
        formPanel.add(lblLastName);
        
        lastNameField = new JTextField();
        formPanel.add(lastNameField);
        lastNameField.setColumns(10);
        
        // Birth Date
        JLabel lblBirthDate = new JLabel("Birth Date (dd/mm/yyyy):");
        formPanel.add(lblBirthDate);
        
        birthDateField = new JFormattedTextField(dateFormat);
        formPanel.add(birthDateField);
        
        // Address
        JLabel lblAddress = new JLabel("Address:");
        formPanel.add(lblAddress);
        
        addressField = new JTextField();
        formPanel.add(addressField);
        addressField.setColumns(10);
        
        // Phone
        JLabel lblPhone = new JLabel("Phone:");
        formPanel.add(lblPhone);
        
        phoneField = new JTextField();
        formPanel.add(phoneField);
        phoneField.setColumns(10);
        
        // Email
        JLabel lblEmail = new JLabel("Email:");
        formPanel.add(lblEmail);
        
        emailField = new JTextField();
        formPanel.add(emailField);
        emailField.setColumns(10);
        
        // Gender
        JLabel lblGender = new JLabel("Gender:");
        formPanel.add(lblGender);
        
        genderComboBox = new JComboBox<>(Gender.values());
        formPanel.add(genderComboBox);
        
        // Work Start Date
        JLabel lblWorkStartDate = new JLabel("Work Start Date (dd/mm/yyyy):");
        formPanel.add(lblWorkStartDate);
        
        workStartDateField = new JFormattedTextField(dateFormat);
        formPanel.add(workStartDateField);
        
        // Salary
        JLabel lblSalary = new JLabel("Salary:");
        formPanel.add(lblSalary);
        
        salaryField = new JTextField();
        formPanel.add(salaryField);
        salaryField.setColumns(10);
        
        // Position
        JLabel lblPosition = new JLabel("Position:");
        formPanel.add(lblPosition);
        
        positionComboBox = new JComboBox<>(Position.values());
        formPanel.add(positionComboBox);
        
        // Department
        JLabel lblDepartment = new JLabel("Department:");
        formPanel.add(lblDepartment);
        
        departmentComboBox = new JComboBox<>();
        updateDepartmentComboBox();
        formPanel.add(departmentComboBox);
        
        // Add photo panel and form panel to the top panel
        topPanel.add(photoPanel, BorderLayout.WEST);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add top panel to content pane
        contentPane.add(topPanel, BorderLayout.NORTH);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton = new JButton("Add");
        addButton.setForeground(new Color(55, 81, 159));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        buttonPanel.add(addButton);
        
        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEmployee();
            }
        });
        buttonPanel.add(updateButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.setForeground(new Color(185, 10, 14));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
        buttonPanel.add(deleteButton);
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        buttonPanel.add(clearButton);
        
        // Table Panel
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Define table columns
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Birth Date");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Position");
        tableModel.addColumn("Salary");
        tableModel.addColumn("Start Date");
        tableModel.addColumn("Has Photo");
        
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.getTableHeader().setResizingAllowed(true);
        
        // Add selection listener to populate form when row is selected
        employeeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                    populateFormFromTable();
                }
            }
        });
        
        scrollPane.setViewportView(employeeTable);
        
        // Load employees into table
        refreshEmployeeTable();
    }
    
    /**
     * Opens a file chooser to select a photo
     */
    private void selectPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Photo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Read and resize the image
                BufferedImage originalImage = ImageIO.read(selectedFile);
                BufferedImage resizedImage = resizeImage(originalImage, PHOTO_WIDTH, PHOTO_HEIGHT);
                
                // Convert image to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", baos);
                selectedPhoto = baos.toByteArray();
                
                // Display the image
                displayPhoto(selectedPhoto);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error loading image: " + e.getMessage(), 
                        "Image Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Displays the photo in the photo label
     */
    private void displayPhoto(byte[] photoData) {
        if (photoData != null && photoData.length > 0) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(photoData));
                ImageIcon icon = new ImageIcon(img);
                photoLabel.setIcon(icon);
                photoLabel.setText("");
            } catch (IOException e) {
                photoLabel.setIcon(null);
                photoLabel.setText("Error displaying photo");
            }
        } else {
            photoLabel.setIcon(null);
            photoLabel.setText("No Photo Selected");
        }
    }
    
    /**
     * Resizes an image to specified dimensions
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
    
    /**
     * Updates the department combo box with available departments
     */
    private void updateDepartmentComboBox() {
        departmentComboBox.removeAllItems();
        departmentComboBox.addItem(null); // Add null option for no department
        
        // Add all departments from the court
        for (Department department : court.getAllDepartments().values()) {
            departmentComboBox.addItem(department);
        }
    }
    
    /**
     * Refreshes the employee table with current data
     */
    private void refreshEmployeeTable() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Add all employees to the table
        for (Employee employee : court.getAllEmployees().values()) {
            tableModel.addRow(new Object[] {
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                dateFormat.format(employee.getBirthDate()),
                employee.getGender(),
                employee.getPosition(),
                employee.getSalary(),
                dateFormat.format(employee.getWorkStartDate()),
                employee.hasPhoto() ? "Yes" : "No"
            });
        }
    }
    
    /**
     * Populates the form fields from the selected table row
     */
    private void populateFormFromTable() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int employeeId = (int) employeeTable.getValueAt(selectedRow, 0);
            Employee employee = court.getRealEmployee(employeeId);
            
            if (employee != null) {
                idField.setText(String.valueOf(employee.getId()));
                idField.setEditable(false); // Cannot edit ID for existing employee
                
                firstNameField.setText(employee.getFirstName());
                lastNameField.setText(employee.getLastName());
                birthDateField.setText(dateFormat.format(employee.getBirthDate()));
                addressField.setText(employee.getAddress());
                phoneField.setText(employee.getPhoneNumber());
                emailField.setText(employee.getEmail());
                genderComboBox.setSelectedItem(employee.getGender());
                workStartDateField.setText(dateFormat.format(employee.getWorkStartDate()));
                salaryField.setText(String.valueOf(employee.getSalary()));
                positionComboBox.setSelectedItem(employee.getPosition());
                
                // If employee has a department, select it
                if (!employee.getDepartments().isEmpty()) {
                    departmentComboBox.setSelectedItem(employee.getDepartments().iterator().next());
                } else {
                    departmentComboBox.setSelectedItem(null);
                }
                
                // Load photo if available
                if (employee.hasPhoto()) {
                    selectedPhoto = employee.getPhoto();
                    displayPhoto(selectedPhoto);
                } else {
                    selectedPhoto = null;
                    photoLabel.setIcon(null);
                    photoLabel.setText("No Photo Available");
                }
            }
        }
    }
    
    /**
     * Adds a new employee with the form data
     */
    private void addEmployee() {
        try {
            // Validate form data
            if (!validateForm()) {
                return;
            }
            
            // Parse field values
            int id = Integer.parseInt(idField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            Date birthDate = dateFormat.parse(birthDateField.getText());
            
            // Check for future date
            if (birthDate.after(new Date())) {
                throw new exceptions.FutureDateException("Birth date cannot be in the future");
            }
            
            String address = addressField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Gender gender = (Gender) genderComboBox.getSelectedItem();
            Date workStartDate = dateFormat.parse(workStartDateField.getText());
            
            // Check for future date
            if (workStartDate.after(new Date())) {
                throw new exceptions.FutureDateException("Work start date cannot be in the future");
            }
            
            double salary = Double.parseDouble(salaryField.getText());
            
            // Check for negative salary
            if (salary < 0) {
                throw new exceptions.NegativeSalaryException("Salary cannot be negative");
            }
            
            Position position = (Position) positionComboBox.getSelectedItem();
            
            // Check if employee already exists
            if (court.getAllEmployees().containsKey(id)) {
                throw new exceptions.ObjectAlreadyExistsException("Employee with ID " + id + " already exists");
            }
            
            // Create new employee
            Employee employee;
            if (selectedPhoto != null) {
                // Create employee with photo
                employee = new Employee(id, firstName, lastName, birthDate, address, 
                        phone, email, gender, workStartDate, salary, position);
                employee.setPhoto(selectedPhoto);
            } else {
                // Create employee without photo
                employee = new Employee(id, firstName, lastName, birthDate, address, 
                        phone, email, gender, workStartDate, salary, position);
            }
            
            // Add selected department to employee if one is selected
            Department selectedDepartment = (Department) departmentComboBox.getSelectedItem();
            if (selectedDepartment != null) {
                employee.addDepartment(selectedDepartment);
            }
            
            // Add employee to court
            if (court.addEmployee(employee)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshEmployeeTable();
                clearForm();
                
                // Save changes
                Main.save();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee. Employee may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (exceptions.FutureDateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (exceptions.NegativeSalaryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Salary Error", JOptionPane.ERROR_MESSAGE);
        } catch (exceptions.ObjectAlreadyExistsException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Object Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/mm/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in ID or Salary field.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Updates an existing employee with the form data
     */
    private void updateEmployee() {
        try {
            // Validate form data
            if (!validateForm()) {
                return;
            }
            
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an employee to update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get the employee ID from the selected row
            int employeeId = (int) employeeTable.getValueAt(selectedRow, 0);
            Employee employee = court.getRealEmployee(employeeId);
            
            // Check if employee exists
            if (employee == null) {
                throw new exceptions.ObjectDoesNotExistException("Employee with ID " + employeeId + " does not exist");
            }
            
            // Prepare to update employee fields
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            
            Date birthDate = dateFormat.parse(birthDateField.getText());
            // Check for future date
            if (birthDate.after(new Date())) {
                throw new exceptions.FutureDateException("Birth date cannot be in the future");
            }
            
            String address = addressField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Gender gender = (Gender) genderComboBox.getSelectedItem();
            
            Date workStartDate = dateFormat.parse(workStartDateField.getText());
            // Check for future date
            if (workStartDate.after(new Date())) {
                throw new exceptions.FutureDateException("Work start date cannot be in the future");
            }
            
            double salary = Double.parseDouble(salaryField.getText());
            // Check for negative salary
            if (salary < 0) {
                throw new exceptions.NegativeSalaryException("Salary cannot be negative");
            }
            
            Position position = (Position) positionComboBox.getSelectedItem();
            
            // Update employee fields
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setBirthDate(birthDate);
            employee.setAddress(address);
            employee.setPhoneNumber(phone);
            employee.setEmail(email);
            employee.setGender(gender);
            employee.setWorkStartDate(workStartDate);
            employee.setSalary(salary);
            employee.setPosition(position);
            
            // Update photo if one is selected
            if (selectedPhoto != null) {
                employee.setPhoto(selectedPhoto);
            }
            
            // Update department if changed
            Department selectedDepartment = (Department) departmentComboBox.getSelectedItem();
            
            // Clear existing departments and add the new one if selected
            for (Department dept : new HashSet<>(employee.getDepartments())) {
                employee.removeDepartment(dept);
            }
            
            if (selectedDepartment != null) {
                employee.addDepartment(selectedDepartment);
            }
            
            // Save changes
            Main.save();
            
            JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshEmployeeTable();
            clearForm();
            
        } catch (exceptions.FutureDateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (exceptions.NegativeSalaryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Salary Error", JOptionPane.ERROR_MESSAGE);
        } catch (exceptions.ObjectDoesNotExistException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Object Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/mm/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in Salary field.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes the selected employee
     */
    private void deleteEmployee() {
        try {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int employeeId = (int) employeeTable.getValueAt(selectedRow, 0);
            Employee employee = court.getRealEmployee(employeeId);
            
            // Check if employee exists
            if (employee == null) {
                throw new exceptions.ObjectDoesNotExistException("Employee with ID " + employeeId + " does not exist");
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete employee " + employee.getFirstName() + " " + employee.getLastName() + "?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (court.removeEmployee(employee)) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshEmployeeTable();
                    clearForm();
                    
                    // Save changes
                    Main.save();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (exceptions.ObjectDoesNotExistException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Object Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Clears all form fields and enables ID field for new entries
     */
    private void clearForm() {
        idField.setText("");
        idField.setEditable(true);
        firstNameField.setText("");
        lastNameField.setText("");
        birthDateField.setText("");
        addressField.setText("");
        phoneField.setText("");
        emailField.setText("");
        genderComboBox.setSelectedIndex(0);
        workStartDateField.setText("");
        salaryField.setText("");
        positionComboBox.setSelectedIndex(0);
        departmentComboBox.setSelectedItem(null);
        
        // Clear photo
        selectedPhoto = null;
        photoLabel.setIcon(null);
        photoLabel.setText("No Photo Selected");
        
        // Clear table selection
        employeeTable.clearSelection();
    }
    
    /**
     * Validates the form data before saving
     * @return true if form data is valid, false otherwise
     */
    private boolean validateForm() {
        // Check for empty required fields
        if (idField.getText().isEmpty() || 
            firstNameField.getText().isEmpty() || 
            lastNameField.getText().isEmpty() || 
            birthDateField.getText().isEmpty() || 
            addressField.getText().isEmpty() || 
            phoneField.getText().isEmpty() || 
            emailField.getText().isEmpty() || 
            workStartDateField.getText().isEmpty() || 
            salaryField.getText().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "All fields are required except Department.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate ID format
        try {
            int id = Integer.parseInt(idField.getText());
            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "ID must be a positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID must be a valid integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate salary format
        try {
            double salary = Double.parseDouble(salaryField.getText());
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "Salary cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate date formats
        try {
            Date birthDate = dateFormat.parse(birthDateField.getText());
            Date workStartDate = dateFormat.parse(workStartDateField.getText());
            
            // Validate birth date is not in the future
            if (birthDate.after(new Date())) {
                JOptionPane.showMessageDialog(this, "Birth date cannot be in the future.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate work start date is not in the future
            if (workStartDate.after(new Date())) {
                JOptionPane.showMessageDialog(this, "Work start date cannot be in the future.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate employee is at least 18 years old
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, -18);
            Date minBirthDate = cal.getTime();
            
            if (birthDate.after(minBirthDate)) {
                JOptionPane.showMessageDialog(this, "Employee must be at least 18 years old.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/mm/yyyy.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
}