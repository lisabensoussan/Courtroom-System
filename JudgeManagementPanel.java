package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import control.Court;
import enums.*;
import model.*;
import utils.UtilsMethods;

public class JudgeManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable judgeTable;
    private DefaultTableModel tableModel;
    private JTextField idField, firstNameField, lastNameField, phoneField, emailField, salaryField;
    private JComboBox<Gender> genderComboBox;
    private JComboBox<Specialization> specializationComboBox;
    private JTextField experienceField;
    private JFormattedTextField birthDateField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private JLabel photoLabel;
    private byte[] selectedPhoto;
    private final int PHOTO_WIDTH = 150;
    private final int PHOTO_HEIGHT = 150;
    private JButton selectPhotoButton;

    public JudgeManagementPanel() {
        court = Main.court;
        
        setTitle("Judge Management");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 900, 700);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create the top panel that will contain both photo and form
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Create the photo panel on the left
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
        
        // Create the form panel on the right
        JPanel formPanel = new JPanel();
        formPanel.setBorder(new TitledBorder(null, "Judge Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        formPanel.setLayout(new GridLayout(0, 4, 10, 10));
        
        // Add form fields
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
        birthDateField = new JFormattedTextField(dateFormat);
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
        
        formPanel.add(new JLabel("Experience (Years):"));
        experienceField = new JTextField();
        formPanel.add(experienceField);
        
        formPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        formPanel.add(salaryField);
        
        // Add photo panel and form panel to top panel
        topPanel.add(photoPanel, BorderLayout.WEST);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add top panel to content pane
        contentPane.add(topPanel, BorderLayout.NORTH);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addJudge());
        buttonPanel.add(addButton);
        
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateJudge());
        buttonPanel.add(updateButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteJudge());
        buttonPanel.add(deleteButton);
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);
        
        // Create table model FIRST (before adding columns)
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Birth Date");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Email");
        tableModel.addColumn("Specialization");
        tableModel.addColumn("Experience (Years)");
        tableModel.addColumn("Salary");
        tableModel.addColumn("Has Photo"); // Add photo column last
        
        // Create table with the model
        judgeTable = new JTable(tableModel);
        judgeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        judgeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateForm();
            }
        });
        
        contentPane.add(new JScrollPane(judgeTable), BorderLayout.CENTER);
        
        // Load judges data
        loadJudgesData();
    }
    
    /**
     * Load all judges data into the table
     */
    private void loadJudgesData() {
        tableModel.setRowCount(0);
        
        for (Lawyer lawyer : court.getAllLawyers().values()) {
            if (lawyer instanceof Judge) {
                Judge judge = (Judge) lawyer;
                tableModel.addRow(new Object[]{
                    judge.getId(), 
                    judge.getFirstName(), 
                    judge.getLastName(), 
                    judge.getBirthDate(),
                    judge.getGender(), 
                    judge.getPhoneNumber(), 
                    judge.getEmail(),
                    judge.getSpecialization(), 
                    judge.getExperienceYear(), 
                    judge.getSalary(),
                    judge.hasPhoto() ? "Yes" : "No"
                });
            }
        }
    }

    /**
     * Populate form fields with selected judge data
     */
    private void populateForm() {
        int selectedRow = judgeTable.getSelectedRow();
        if (selectedRow == -1) return; // No selection, exit method

        int judgeId = (int) judgeTable.getValueAt(selectedRow, 0);
        Judge judge = court.getRealJudge(judgeId);

        if (judge != null) {
            idField.setText(String.valueOf(judge.getId()));
            firstNameField.setText(judge.getFirstName());
            lastNameField.setText(judge.getLastName());
            birthDateField.setText(dateFormat.format(judge.getBirthDate())); // Format date correctly
            genderComboBox.setSelectedItem(judge.getGender());
            phoneField.setText(judge.getPhoneNumber());
            emailField.setText(judge.getEmail());
            specializationComboBox.setSelectedItem(judge.getSpecialization());
            experienceField.setText(String.valueOf(judge.getExperienceYear()));
            salaryField.setText(String.valueOf(judge.getSalary()));

            // Load photo if available
            if (judge.hasPhoto()) {
                selectedPhoto = judge.getPhoto();
                displayPhoto(selectedPhoto);
            } else {
                selectedPhoto = null;
                photoLabel.setIcon(null);
                photoLabel.setText("No Photo Available");
            }

            idField.setEditable(false); // Prevent ID modification for existing judges
        }
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
     * Validate form input
     */
    private boolean validateForm() {
        // Ensure all required fields are filled
        if (idField.getText().isEmpty() || 
            firstNameField.getText().isEmpty() || 
            lastNameField.getText().isEmpty() || 
            birthDateField.getText().isEmpty() || 
            phoneField.getText().isEmpty() || 
            emailField.getText().isEmpty() || 
            experienceField.getText().isEmpty() || 
            salaryField.getText().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate numeric fields
        try {
            int id = Integer.parseInt(idField.getText());
            int experience = Integer.parseInt(experienceField.getText());
            double salary = Double.parseDouble(salaryField.getText());

            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "ID must be a positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (experience < 0) {
                JOptionPane.showMessageDialog(this, "Experience years cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "Salary cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID, Experience, and Salary must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate birth date format
        try {
            Date birthDate = dateFormat.parse(birthDateField.getText());
            if (birthDate.after(new Date())) {
                JOptionPane.showMessageDialog(this, "Birth date cannot be in the future.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid birth date format. Use dd/MM/yyyy.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true; // All validations passed
    }

    /**
     * Add a new judge
     */
    private void addJudge() {
        try {
            // Validate input fields
            if (!validateForm()) {
                return;
            }

            // Parse form values
            int id = Integer.parseInt(idField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            Date birthDate = dateFormat.parse(birthDateField.getText());
            
            // Ensure birth date is not in the future
            if (birthDate.after(new Date())) {
                throw new exceptions.FutureDateException("Birth date cannot be in the future");
            }

            Gender gender = (Gender) genderComboBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Specialization specialization = (Specialization) specializationComboBox.getSelectedItem();
            int experience = Integer.parseInt(experienceField.getText());
            double salary = Double.parseDouble(salaryField.getText());

            // Ensure salary and experience are valid
            if (salary < 0) {
                throw new exceptions.NegativeSalaryException("Salary cannot be negative");
            }
            if (experience < 0) {
                throw new IllegalArgumentException("Experience years cannot be negative");
            }

            // Ensure no duplicate ID
            if (court.getRealJudge(id) != null) {
                JOptionPane.showMessageDialog(this, "A judge with this ID already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create the new Judge object
            Judge judge = new Judge(id, firstName, lastName, birthDate, "", phone, email, gender, specialization, 0, salary, experience);
            
            // Set photo if available
            if (selectedPhoto != null) {
                judge.setPhoto(selectedPhoto);
            }

            // Add judge to the court system
            if (court.addJudge(judge)) {
                // Refresh the table
                loadJudgesData();

                JOptionPane.showMessageDialog(this, "Judge added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                
                // Save changes
                Main.save();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add judge.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (exceptions.FutureDateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (exceptions.NegativeSalaryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Salary Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in ID, Experience, or Salary field.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/MM/yyyy.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Update an existing judge
     */
    private void updateJudge() {
        try {
            // Ensure a judge is selected
            int selectedRow = judgeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a judge to update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate input fields
            if (!validateForm()) {
                return;
            }

            // Retrieve the judge ID from the table
            int judgeId = Integer.parseInt(judgeTable.getValueAt(selectedRow, 0).toString());
            Judge judge = court.getRealJudge(judgeId);

            // Ensure the judge exists
            if (judge == null) {
                JOptionPane.showMessageDialog(this, "Judge with ID " + judgeId + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate and parse updated values
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            Date birthDate = dateFormat.parse(birthDateField.getText());

            // Ensure birth date is not in the future
            if (birthDate.after(new Date())) {
                throw new exceptions.FutureDateException("Birth date cannot be in the future");
            }

            Gender gender = (Gender) genderComboBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Specialization specialization = (Specialization) specializationComboBox.getSelectedItem();
            int experience = Integer.parseInt(experienceField.getText());
            double salary = Double.parseDouble(salaryField.getText());
            
            // Ensure salary and experience are valid
            if (experience < 0) {
                throw new IllegalArgumentException("Experience years cannot be negative");
            }
            if (salary < 0) {
                throw new exceptions.NegativeSalaryException("Salary cannot be negative");
            }

            // Update judge attributes
            judge.setFirstName(firstName);
            judge.setLastName(lastName);
            judge.setBirthDate(birthDate);
            judge.setGender(gender);
            judge.setPhoneNumber(phone);
            judge.setEmail(email);
            judge.setSpecialization(specialization);
            judge.setExperienceYear(experience);
            judge.setSalary(salary);
            
            // Update photo if one is selected
            if (selectedPhoto != null) {
                judge.setPhoto(selectedPhoto);
            }

            // Save the updated data
            Main.save();

            // Refresh table to reflect changes
            loadJudgesData();
            clearForm();

            JOptionPane.showMessageDialog(this, "Judge updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (exceptions.FutureDateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (exceptions.NegativeSalaryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Salary Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in Experience or Salary field.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd/MM/yyyy.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
        
    /**
     * Delete a judge
     */
    private void deleteJudge() {
        try {
            // Ensure a judge is selected
            int selectedRow = judgeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a judge to delete.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Retrieve the judge ID from the selected row
            int judgeId = Integer.parseInt(judgeTable.getValueAt(selectedRow, 0).toString());
            Judge judge = court.getRealJudge(judgeId);

            // Ensure the judge exists
            if (judge == null) {
                JOptionPane.showMessageDialog(this, "Judge with ID " + judgeId + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmation dialog before deleting
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Judge " + judge.getFirstName() + " " + judge.getLastName() + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Attempt to remove the judge
                if (court.removeJudge(judge)) {
                    JOptionPane.showMessageDialog(this, "Judge deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadJudgesData(); // Refresh the table
                    clearForm(); // Clear input fields
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete judge.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in ID field.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Clear all form fields
     */
    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        birthDateField.setText("");
        phoneField.setText("");
        emailField.setText("");
        salaryField.setText("");
        genderComboBox.setSelectedIndex(0);
        specializationComboBox.setSelectedIndex(0);
        experienceField.setText("");
        judgeTable.clearSelection();
        selectedPhoto = null;
        photoLabel.setIcon(null);
        photoLabel.setText("No Photo Selected");
        idField.setEditable(true); // Make ID editable for new entries
    }
}