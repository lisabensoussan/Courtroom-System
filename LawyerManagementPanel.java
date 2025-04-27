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
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import control.Court;
import enums.Gender;
import enums.Specialization;
import model.Department;
import model.Lawyer;
import utils.UtilsMethods;

public class LawyerManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable lawyerTable;
    private DefaultTableModel tableModel;
    private JTextField idField, firstNameField, lastNameField, birthDateField, phoneField, emailField, salaryField;
    private JComboBox<Gender> genderComboBox;
    private JComboBox<Specialization> specializationComboBox;
    private JComboBox<Department> departmentComboBox;
    private JButton addButton, updateButton, deleteButton, clearButton, selectPhotoButton;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Photo related components
    private JLabel photoLabel;
    private byte[] selectedPhoto;
    private final int PHOTO_WIDTH = 150;
    private final int PHOTO_HEIGHT = 150;

    public LawyerManagementPanel() {
        super("Lawyer Management", true, true, true, true);
        court = Main.court;
        initializeUI();
        loadLawyersData();
    }

    private void initializeUI() {
        setSize(900, 700);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create a panel for the form with photo
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Photo panel (left side)
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
        
        topPanel.add(photoPanel, BorderLayout.WEST);
        
        // Form fields (right side of the top panel)
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(new TitledBorder("Lawyer Information"));

        formPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);
        
        formPanel.add(new JLabel("Birth Date (dd/MM/yyyy):"));
        birthDateField = new JTextField();
        formPanel.add(birthDateField);

        formPanel.add(new JLabel("Gender:"));
        genderComboBox = new JComboBox<>(Gender.values());
        formPanel.add(genderComboBox);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
     
        formPanel.add(new JLabel("Specialization:"));
        specializationComboBox = new JComboBox<>(Specialization.values());
        formPanel.add(specializationComboBox);

        formPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        formPanel.add(salaryField);

        formPanel.add(new JLabel("Department:"));
        departmentComboBox = new JComboBox<>();
        updateDepartmentComboBox();
        formPanel.add(departmentComboBox);
        
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addLawyer());
        updateButton.addActionListener(e -> updateLawyer());
        deleteButton.addActionListener(e -> deleteLawyer());
        clearButton.addActionListener(e -> clearForm());

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Birth Date");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Email");
        tableModel.addColumn("Specialization");
        tableModel.addColumn("Salary");
        tableModel.addColumn("Department");
        tableModel.addColumn("Has Photo");

        lawyerTable = new JTable(tableModel);
        lawyerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lawyerTable.getSelectionModel().addListSelectionListener(e -> populateForm());
        add(new JScrollPane(lawyerTable), BorderLayout.CENTER);
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
    
    private void updateDepartmentComboBox() {
        departmentComboBox.removeAllItems();
        for (Department department : court.getAllDepartments().values()) {
            departmentComboBox.addItem(department);
        }
    }

    private void populateForm() {
        int selectedRow = lawyerTable.getSelectedRow();
        if (selectedRow == -1) return;

        idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        firstNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        lastNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());

        // Ensure the birth date is formatted correctly before setting it
        Date birthDate = (Date) tableModel.getValueAt(selectedRow, 3);
        birthDateField.setText(dateFormat.format(birthDate)); 

        genderComboBox.setSelectedItem(Gender.valueOf(tableModel.getValueAt(selectedRow, 4).toString()));
        phoneField.setText(tableModel.getValueAt(selectedRow, 5).toString());
        emailField.setText(tableModel.getValueAt(selectedRow, 6).toString());
        specializationComboBox.setSelectedItem(Specialization.valueOf(tableModel.getValueAt(selectedRow, 7).toString()));
        salaryField.setText(tableModel.getValueAt(selectedRow, 8).toString());

        String departmentName = tableModel.getValueAt(selectedRow, 9).toString();
        for (int i = 0; i < departmentComboBox.getItemCount(); i++) {
            if (departmentComboBox.getItemAt(i).getName().equals(departmentName)) {
                departmentComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        // Load photo if available
        int id = Integer.parseInt(idField.getText());
        Lawyer lawyer = court.getRealLawyer(id);
        if (lawyer != null && lawyer.hasPhoto()) {
            selectedPhoto = lawyer.getPhoto();
            displayPhoto(selectedPhoto);
        } else {
            selectedPhoto = null;
            photoLabel.setIcon(null);
            photoLabel.setText("No Photo Available");
        }
    }

    private void loadLawyersData() {
        tableModel.setRowCount(0);
        for (Lawyer lawyer : court.getAllLawyers().values()) {
            tableModel.addRow(new Object[]{
                lawyer.getId(), lawyer.getFirstName(), lawyer.getLastName(), lawyer.getBirthDate(), lawyer.getGender(),
                lawyer.getPhoneNumber(), lawyer.getEmail(), lawyer.getSpecialization(), lawyer.getSalary(),
                lawyer.getDepartment() != null ? lawyer.getDepartment().getName() : "Unassigned",
                lawyer.hasPhoto() ? "Yes" : "No"
            });
        }
    }

    private void addLawyer() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            Date birthDate = dateFormat.parse(birthDateField.getText().trim());
            Gender gender = (Gender) genderComboBox.getSelectedItem();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            Specialization specialization = (Specialization) specializationComboBox.getSelectedItem();
            double salary = Double.parseDouble(salaryField.getText().trim());
            Department department = (Department) departmentComboBox.getSelectedItem();

            // Ensure all fields are filled
            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ensure a valid department is selected
            if (department == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid department.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if lawyer already exists
            if (court.getAllLawyers().containsKey(id)) {
                JOptionPane.showMessageDialog(this, "Lawyer with this ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create Lawyer Object with photo if available
            Lawyer lawyer;
            if (selectedPhoto != null) {
                lawyer = new Lawyer(id, firstName, lastName, birthDate, "", phone, email, gender, specialization, 0, salary, selectedPhoto);
            } else {
                lawyer = new Lawyer(id, firstName, lastName, birthDate, "", phone, email, gender, specialization, 0, salary);
            }

            if (court.addLawyer(lawyer)) {
                court.addLawyerToDepartment(department, lawyer);
                loadLawyersData();
                JOptionPane.showMessageDialog(this, "Lawyer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                Main.save();
                
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add lawyer. Check system constraints.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Failed to add lawyer due to system constraints.");
            }

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format! Check ID and Salary fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLawyer() {
        int selectedRow = lawyerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a lawyer to update.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            Date birthDate = dateFormat.parse(birthDateField.getText());
            Gender gender = (Gender) genderComboBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Specialization specialization = (Specialization) specializationComboBox.getSelectedItem();
            double salary = Double.parseDouble(salaryField.getText());
            Department department = (Department) departmentComboBox.getSelectedItem();

            Lawyer lawyer = court.getAllLawyers().get(id);
            if (lawyer != null) {
                lawyer.setFirstName(firstName);
                lawyer.setLastName(lastName);
                lawyer.setBirthDate(birthDate);
                lawyer.setGender(gender);
                lawyer.setPhoneNumber(phone);
                lawyer.setEmail(email);
                lawyer.setSpecialization(specialization);
                lawyer.setSalary(salary);
                
                // Update photo if one is selected
                if (selectedPhoto != null) {
                    lawyer.setPhoto(selectedPhoto);
                }

                if (department != null) {
                    court.addLawyerToDepartment(department, lawyer); // Ensuring proper assignment
                }

                // Save the updated data
                Main.save();

                loadLawyersData();
                JOptionPane.showMessageDialog(this, "Lawyer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lawyer not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Please use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format! Check ID and Salary fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLawyer() {
        int selectedRow = lawyerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a lawyer to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(lawyerTable.getValueAt(selectedRow, 0).toString());
        Lawyer lawyer = court.getRealLawyer(id);
        if (lawyer != null && court.removeLawyer(lawyer)) {
            loadLawyersData();
            clearForm();
        }
    }

    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        birthDateField.setText("");
        phoneField.setText("");
        emailField.setText("");
        salaryField.setText("");
        selectedPhoto = null;
        photoLabel.setIcon(null);
        photoLabel.setText("No Photo Selected");
    }
    
    
}