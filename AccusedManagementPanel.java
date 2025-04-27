package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import control.Court;
import enums.Gender;
import model.*;

/**
 * Corrected version of AccusedManagementPanel with proper GridBagConstraints handling
 */
public class AccusedManagementPanel extends JInternalFrame {
	private static final long serialVersionUID = 1L;
    private JTable accusedTable;
    private DefaultTableModel tableModel;
    private JTextField idField, firstNameField, lastNameField, jobField, addressField,  phoneField, emailField, birthDateField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private JComboBox<Gender> genderComboBox;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Court court;

    /**
     * Create the panel for accused management
     */
    public AccusedManagementPanel() {
        court = Main.court;
        
        setTitle("Accused Management");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 700, 500);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        JPanel formPanel = new JPanel();
        formPanel.setBorder(new TitledBorder("Accused Information"));
        contentPane.add(formPanel, BorderLayout.NORTH);
        formPanel.setLayout(new GridLayout(0, 4, 10, 10));
        
        formPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        formPanel.add(idField);
        
        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);
        
        formPanel.add(new JLabel("Job:"));
        jobField = new JTextField();
        formPanel.add(jobField);
        
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
        
        formPanel.add(new JLabel("Adress:"));
        addressField = new JTextField();
        formPanel.add(addressField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        
        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton = new JButton("Add");
        buttonPanel.add(addButton);
        
        updateButton = new JButton("Update");
        buttonPanel.add(updateButton);
        
        deleteButton = new JButton("Delete");
        buttonPanel.add(deleteButton);
        
        clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);
        
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Address");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Birthdate");
        tableModel.addColumn("Job");
        tableModel.addColumn("Gender");

        
        accusedTable = new JTable(tableModel);
        accusedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(accusedTable);
        
        loadAccusedData();
        
        addButton.addActionListener(e -> addAccused());
        updateButton.addActionListener(e -> updateAccused());
        deleteButton.addActionListener(e -> deleteAccused());
        clearButton.addActionListener(e -> clearForm());
        
        
        accusedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });

    }
    
    private void populateFormFromSelectedRow() {
        int selectedRow = accusedTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(accusedTable.getValueAt(selectedRow, 0).toString());
            firstNameField.setText(accusedTable.getValueAt(selectedRow, 1).toString());
            lastNameField.setText(accusedTable.getValueAt(selectedRow, 2).toString());
            addressField.setText(accusedTable.getValueAt(selectedRow, 3).toString());
            emailField.setText(accusedTable.getValueAt(selectedRow, 4).toString());
            phoneField.setText(accusedTable.getValueAt(selectedRow, 5).toString());

            String birthDateStr = accusedTable.getValueAt(selectedRow, 6).toString();
            if (!birthDateStr.equals("N/A")) {
                birthDateField.setText(birthDateStr);
            } else {
                birthDateField.setText("");
            }

            jobField.setText(accusedTable.getValueAt(selectedRow, 7).toString());
            genderComboBox.setSelectedItem(Gender.valueOf(accusedTable.getValueAt(selectedRow, 8).toString()));
        }
    }


    /**
     * Loads accused individuals into the table
     */
    private void loadAccusedData() {
        tableModel.setRowCount(0); // Clear the table before reloading

        for (Accused accused : court.getAllAccuseds().values()) {
            tableModel.addRow(new Object[]{
                accused.getId(),
                accused.getFirstName(),
                accused.getLastName(),
                accused.getAddress(),
                accused.getEmail(),
                accused.getPhoneNumber(),
                accused.getBirthDate() != null ? dateFormat.format(accused.getBirthDate()) : "N/A",
                accused.getJob(),
                accused.getGender(),
                
                
                
            });
        }
    }

    private void addAccused() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String job = jobField.getText().trim();
            String address = addressField.getText().trim();
            String phoneNumber = phoneField.getText().trim();
            String email = emailField.getText().trim();
            Date birthDate = dateFormat.parse(birthDateField.getText().trim());
            Gender gender = (Gender) genderComboBox.getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || job.isEmpty() || address.isEmpty() ||
                phoneNumber.isEmpty() || email.isEmpty() || birthDate == null || gender == null) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (court.getRealAccused(id) != null) {
                JOptionPane.showMessageDialog(this, "An accused with this ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Accused accused = new Accused(id, firstName, lastName, birthDate, address, phoneNumber, email, gender, job);

            if (court.addAccused(accused)) {
                JOptionPane.showMessageDialog(this, "Accused added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAccusedData();
                clearForm();
                Main.save();

            } else {
                JOptionPane.showMessageDialog(this, "Error adding accused.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void updateAccused() {
        try {
            int selectedRow = accusedTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an accused to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idField.getText().trim());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String job = jobField.getText().trim();
            String address = addressField.getText().trim();
            String phoneNumber = phoneField.getText().trim();
            String email = emailField.getText().trim();
            Date birthDate = dateFormat.parse(birthDateField.getText().trim());
            Gender gender = (Gender) genderComboBox.getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || job.isEmpty() || address.isEmpty() ||
                phoneNumber.isEmpty() || email.isEmpty() || birthDate == null || gender == null) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Accused accused = court.getRealAccused(id);
            if (accused == null) {
                JOptionPane.showMessageDialog(this, "Accused not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            accused.setFirstName(firstName);
            accused.setLastName(lastName);
            accused.setJob(job);
            accused.setAddress(address);
            accused.setPhoneNumber(phoneNumber);
            accused.setEmail(email);
            accused.setBirthDate(birthDate);
            accused.setGender(gender);

            JOptionPane.showMessageDialog(this, "Accused updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAccusedData();
            clearForm();

            Main.save();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void deleteAccused() {
        int selectedRow = accusedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an accused to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(accusedTable.getValueAt(selectedRow, 0).toString());
        Accused accused = court.getRealAccused(id);
        
        if (accused != null && court.removeAccused(accused)) {
            JOptionPane.showMessageDialog(this, "Accused deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAccusedData();
            clearForm();

            // ✅ Save the updated court data to persist changes
            Main.save();

        } else {
            JOptionPane.showMessageDialog(this, "Error deleting accused.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        jobField.setText("");

    }
}
