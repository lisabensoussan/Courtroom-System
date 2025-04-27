package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import control.Court;
import enums.Gender;
import exceptions.*;
import model.*;

/**
 * Panel for anonymous witness testimony submission
 * This is an additional feature that allows public testimony submission without login
 */
public class WitnessTestimonyPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Court court;
    private JTextField idField, firstNameField, lastNameField, addressField, phoneField, emailField;
    private JComboBox<Gender> genderComboBox;
    private JComboBox<String> caseComboBox;
    private JTextArea testimonyArea;
    private JFormattedTextField birthDateField;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Create the frame for public testimony submission.
     */
    public WitnessTestimonyPanel() {
        this.court = Main.court;
        
        setTitle("HRS Court Management System - Witness Testimony Submission");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 700, 650);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setLayout(new BorderLayout());
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        JLabel titleLabel = new JLabel("Witness Testimony Submission");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Submit your testimony without requiring a user account");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Create main form panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // Create witness info panel
        JPanel witnessPanel = new JPanel();
        witnessPanel.setBorder(new TitledBorder(null, "Witness Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        witnessPanel.setLayout(new GridLayout(0, 4, 10, 10));
        mainPanel.add(witnessPanel, BorderLayout.NORTH);
        
        // ID
        JLabel lblId = new JLabel("ID Number:");
        witnessPanel.add(lblId);
        
        idField = new JTextField();
        witnessPanel.add(idField);
        idField.setColumns(10);
        
        // First Name
        JLabel lblFirstName = new JLabel("First Name:");
        witnessPanel.add(lblFirstName);
        
        firstNameField = new JTextField();
        witnessPanel.add(firstNameField);
        firstNameField.setColumns(10);
        
        // Last Name
        JLabel lblLastName = new JLabel("Last Name:");
        witnessPanel.add(lblLastName);
        
        lastNameField = new JTextField();
        witnessPanel.add(lastNameField);
        lastNameField.setColumns(10);
        
        // Birth Date
        JLabel lblBirthDate = new JLabel("Birth Date (dd/mm/yyyy):");
        witnessPanel.add(lblBirthDate);
        
        birthDateField = new JFormattedTextField(dateFormat);
        witnessPanel.add(birthDateField);
        
        // Address
        JLabel lblAddress = new JLabel("Address:");
        witnessPanel.add(lblAddress);
        
        addressField = new JTextField();
        witnessPanel.add(addressField);
        addressField.setColumns(10);
        
        // Phone
        JLabel lblPhone = new JLabel("Phone:");
        witnessPanel.add(lblPhone);
        
        phoneField = new JTextField();
        witnessPanel.add(phoneField);
        phoneField.setColumns(10);
        
        // Email
        JLabel lblEmail = new JLabel("Email:");
        witnessPanel.add(lblEmail);
        
        emailField = new JTextField();
        witnessPanel.add(emailField);
        emailField.setColumns(10);
        
        // Gender
        JLabel lblGender = new JLabel("Gender:");
        witnessPanel.add(lblGender);
        
        genderComboBox = new JComboBox<>(Gender.values());
        witnessPanel.add(genderComboBox);
        
        // Case
        JLabel lblCase = new JLabel("Related Case:");
        witnessPanel.add(lblCase);
        
        caseComboBox = new JComboBox<>();
        updateCaseComboBox();
        witnessPanel.add(caseComboBox);
        
        // Create testimony panel
        JPanel testimonyPanel = new JPanel();
        testimonyPanel.setBorder(new TitledBorder(null, "Testimony Content", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        testimonyPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.add(testimonyPanel, BorderLayout.CENTER);
        
        JLabel lblTestimony = new JLabel("Please enter your testimony below (required):");
        lblTestimony.setBorder(new EmptyBorder(5, 5, 5, 5));
        testimonyPanel.add(lblTestimony, BorderLayout.NORTH);
        
        testimonyArea = new JTextArea();
        testimonyArea.setLineWrap(true);
        testimonyArea.setWrapStyleWord(true);
        testimonyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane testimonyScrollPane = new JScrollPane(testimonyArea);
        testimonyScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        testimonyPanel.add(testimonyScrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton submitButton = new JButton("Submit Testimony");
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(new Color(75, 102, 167));
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setPreferredSize(new Dimension(200, 40));
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitTestimony();
            }
        });
        buttonPanel.add(submitButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(new Color(162, 28, 30));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LOGIN().setVisible(true);
            }
        });
        buttonPanel.add(cancelButton);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(44, 62, 80));
        footerPanel.setLayout(new BorderLayout());
        contentPane.add(footerPanel, BorderLayout.SOUTH);
        
        JLabel privacyLabel = new JLabel("Your information is protected and will only be used for legal proceedings.");
        privacyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        privacyLabel.setForeground(Color.WHITE);
        privacyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        privacyLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        footerPanel.add(privacyLabel, BorderLayout.CENTER);
    }
    
    /**
     * Updates the case combo box with active cases
     */

    private void updateCaseComboBox() {
        caseComboBox.removeAllItems();
        caseComboBox.addItem(""); // Empty option for default selection

        // Add only the case codes (IDs) to the combo box
        for (Case caseObj : court.getAllCases().values()) {
            if (caseObj.getCaseStatus() == enums.Status.inProcess) {
                caseComboBox.addItem(caseObj.getCode()); // Only case ID
            }
        }
    }


    
    
    private void submitTestimony() {
        try {
            if (!validateForm()) {
                return;
            }

            System.out.println("Selected Case from ComboBox: " + caseComboBox.getSelectedItem());

            if (caseComboBox.getSelectedItem() == null || caseComboBox.getSelectedItem().toString().isEmpty()) {
                throw new Exception("Please select a valid case before submitting.");
            }

            int id = Integer.parseInt(idField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            Date birthDate = dateFormat.parse(birthDateField.getText());
            String address = addressField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Gender gender = (Gender) genderComboBox.getSelectedItem();
            String testimonyContent = testimonyArea.getText();

            Witness witness = court.getRealWitness(id);
            if (witness == null) {
                witness = new Witness(id, firstName, lastName, birthDate, address, phone, email, gender);
                court.addWitness(witness);
            }

            System.out.println("Witness: " + witness);

            String caseCode = caseComboBox.getSelectedItem().toString();
            Case selectedCase = court.getRealCase(caseCode);

            if (selectedCase == null) {
                throw new ObjectDoesNotExistException("Selected case does not exist.");
            }

            System.out.println("Selected Case: " + selectedCase);

            Testimony testimony = new Testimony(selectedCase, testimonyContent, witness);

            System.out.println("Attempting to add testimony...");
            boolean success = court.addTestimony(testimony);

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Thank you! Your testimony has been submitted successfully.\n" +
                    "It will be reviewed by the court authorities.", 
                    "Testimony Submitted", JOptionPane.INFORMATION_MESSAGE);
                
                // ✅ Clear form after successful submission
                clearForm();
                
                // ✅ Save the court data
                Main.save();

            } else {
                throw new Exception("Testimony submission failed in court.addTestimony().");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID must be a valid integer number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/MM/yyyy.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (ObjectDoesNotExistException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Case Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }





    
    /**
     * Validates the form data
     * @return true if valid, false otherwise
     */
    private boolean validateForm() {
        // Check required fields
        if (idField.getText().isEmpty() || 
            firstNameField.getText().isEmpty() || 
            lastNameField.getText().isEmpty() || 
            birthDateField.getText().isEmpty() || 
            addressField.getText().isEmpty() || 
            phoneField.getText().isEmpty() || 
            emailField.getText().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                    "All personal information fields are required.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check testimony content
        if (testimonyArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please provide your testimony content.", 
                    "Missing Testimony", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check that a case is selected
        if (caseComboBox.getSelectedItem() == null || 
            caseComboBox.getSelectedItem().toString().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                    "Please select a case for your testimony.", 
                    "Case Selection Required", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Clears the form fields
     */
    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        birthDateField.setText("");
        addressField.setText("");
        phoneField.setText("");
        emailField.setText("");
        genderComboBox.setSelectedIndex(0);
        caseComboBox.setSelectedIndex(0);
        testimonyArea.setText("");
    }
    
    /**
     * Main method to run the witness testimony panel standalone
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WitnessTestimonyPanel frame = new WitnessTestimonyPanel();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
