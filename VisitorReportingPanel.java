package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;

import control.Court;
import model.*;

/**
 * Panel for visitor reporting without login
 * This is an additional feature that allows visitors to report their arrival
 */
public class VisitorReportingPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Court court;
    private JTextField nameField, phoneField;
    private JTextArea purposeArea;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> staffComboBox;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Create the frame for visitor reporting.
     */
    public VisitorReportingPanel() {
        this.court = Main.court;
        
        setTitle("HRS Court Management System - Visitor Check-in");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 500);
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
        
        JLabel titleLabel = new JLabel("Visitor Check-in System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Please enter your information to notify staff of your arrival");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Create main form panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // Create visitor info panel
        JPanel visitorPanel = new JPanel();
        visitorPanel.setBorder(new TitledBorder(null, "Visitor Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        visitorPanel.setLayout(new GridLayout(0, 2, 10, 10));
        mainPanel.add(visitorPanel, BorderLayout.NORTH);
        
        // Name
        JLabel lblName = new JLabel("Full Name:");
        visitorPanel.add(lblName);
        
        nameField = new JTextField();
        visitorPanel.add(nameField);
        
        // Phone
        JLabel lblPhone = new JLabel("Phone Number:");
        visitorPanel.add(lblPhone);
        
        phoneField = new JTextField();
        visitorPanel.add(phoneField);
        
        // Department
        JLabel lblDepartment = new JLabel("Department to Visit:");
        visitorPanel.add(lblDepartment);
        
        departmentComboBox = new JComboBox<>();
        updateDepartmentComboBox();
        departmentComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStaffComboBox();
            }
        });
        visitorPanel.add(departmentComboBox);
        
        // Staff member
        JLabel lblStaff = new JLabel("Staff Member to Visit:");
        visitorPanel.add(lblStaff);
        
        staffComboBox = new JComboBox<>();
        staffComboBox.setEnabled(false); // Disabled until department is selected
        visitorPanel.add(staffComboBox);
        
        // Create purpose panel
        JPanel purposePanel = new JPanel();
        purposePanel.setBorder(new TitledBorder(null, "Visit Purpose", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        purposePanel.setLayout(new BorderLayout(0, 5));
        mainPanel.add(purposePanel, BorderLayout.CENTER);
        
        JLabel lblPurpose = new JLabel("Please briefly describe the purpose of your visit:");
        purposePanel.add(lblPurpose, BorderLayout.NORTH);
        
        purposeArea = new JTextArea();
        purposeArea.setLineWrap(true);
        purposeArea.setWrapStyleWord(true);
        JScrollPane purposeScrollPane = new JScrollPane(purposeArea);
        purposeScrollPane.setPreferredSize(new Dimension(0, 100));
        purposePanel.add(purposeScrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton checkInButton = new JButton("Check In");
        checkInButton.setBackground(new Color(46, 204, 113));
        checkInButton.setForeground(new Color(75, 102, 167));
        checkInButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkInButton.setPreferredSize(new Dimension(120, 40));
        checkInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitVisitorReport();
            }
        });
        buttonPanel.add(checkInButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(new Color(162, 28, 30));
        cancelButton.setPreferredSize(new Dimension(120, 40));
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
        
        JLabel currentTimeLabel = new JLabel("Current time: " + dateFormat.format(new Date()));
        currentTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        currentTimeLabel.setForeground(Color.WHITE);
        currentTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentTimeLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        footerPanel.add(currentTimeLabel, BorderLayout.CENTER);
        
        // Start a timer to update the current time
        Timer timer = new Timer(60000, new ActionListener() { // Update every minute
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTimeLabel.setText("Current time: " + dateFormat.format(new Date()));
            }
        });
        timer.start();
    }
    
    /**
     * Updates the department combo box
     */
    private void updateDepartmentComboBox() {
        departmentComboBox.removeAllItems();
        departmentComboBox.addItem(""); // Empty option
        
        // Add all departments
        for (Department department : court.getAllDepartments().values()) {
            departmentComboBox.addItem(department.getNumber() + " - " + department.getName());
        }
    }
    
    /**
     * Updates the staff combo box based on selected department
     */
    private void updateStaffComboBox() {
        staffComboBox.removeAllItems();
        staffComboBox.addItem(""); // Empty option
        
        String selectedDept = (String) departmentComboBox.getSelectedItem();
        if (selectedDept == null || selectedDept.isEmpty()) {
            staffComboBox.setEnabled(false);
            return;
        }
        
        // Parse department number
        int deptNumber;
        try {
            deptNumber = Integer.parseInt(selectedDept.substring(0, selectedDept.indexOf(" - ")));
        } catch (Exception e) {
            staffComboBox.setEnabled(false);
            return;
        }
        
        // Find department
        Department department = court.getRealDepartment(deptNumber);
        if (department == null) {
            staffComboBox.setEnabled(false);
            return;
        }
        
        // Add employees from department
        for (Employee employee : department.getEmployees()) {
            staffComboBox.addItem(employee.getId() + " - " + employee.getFirstName() + " " + employee.getLastName());
        }
        
        // Add lawyers/judges from department
        for (Lawyer lawyer : department.getLawyers()) {
            String title = lawyer instanceof Judge ? "Judge " : "Lawyer ";
            staffComboBox.addItem(lawyer.getId() + " - " + title + lawyer.getFirstName() + " " + lawyer.getLastName());
        }
        
        staffComboBox.setEnabled(true);
    }
    
    /**
     * Submits the visitor report
     */
    private void submitVisitorReport() {
        try {
            // Validate form
            if (!validateForm()) {
                return;
            }
            
            // Get form data
            String visitorName = nameField.getText();
            String phoneNumber = phoneField.getText();
            String selectedDept = (String) departmentComboBox.getSelectedItem();
            String selectedStaff = (String) staffComboBox.getSelectedItem();
            String purpose = purposeArea.getText();
            Date currentTime = new Date();
            
            // Parse department number
            int deptNumber = Integer.parseInt(selectedDept.substring(0, selectedDept.indexOf(" - ")));
            Department department = court.getRealDepartment(deptNumber);
            
            // Get staff member ID if selected
            int staffId = -1;
            String staffName = "Not specified";
            if (selectedStaff != null && !selectedStaff.isEmpty()) {
                staffId = Integer.parseInt(selectedStaff.substring(0, selectedStaff.indexOf(" - ")));
                staffName = selectedStaff.substring(selectedStaff.indexOf(" - ") + 3);
            }
            
            // Create visitor report object (simplified here since it's not in the model)
            StringBuilder reportMessage = new StringBuilder();
            reportMessage.append("VISITOR CHECK-IN NOTIFICATION\n\n");
            reportMessage.append("Time: ").append(dateFormat.format(currentTime)).append("\n");
            reportMessage.append("Visitor: ").append(visitorName).append("\n");
            reportMessage.append("Phone: ").append(phoneNumber).append("\n");
            reportMessage.append("Department: ").append(department.getName()).append("\n");
            
            if (staffId != -1) {
                reportMessage.append("Staff to Visit: ").append(staffName).append(" (ID: ").append(staffId).append(")\n");
            } else {
                reportMessage.append("Staff to Visit: Not specified\n");
            }
            
            reportMessage.append("\nPurpose of Visit:\n").append(purpose);
            
            // In a real system, this would send the notification to the relevant staff
            // For this simulation, we'll just show a dialog
            
            // Show confirmation to visitor
            JOptionPane.showMessageDialog(this, 
                    "Thank you for checking in!\n\n" +
                    "Your visit has been registered and the staff has been notified.\n" +
                    "Please wait in the reception area until you are called.", 
                    "Check-in Successful", JOptionPane.INFORMATION_MESSAGE);
            
            // Show notification to staff (simulated)
            JTextArea textArea = new JTextArea(reportMessage.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Staff Notification (Simulation)",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error submitting visitor report: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Validates the form data
     * @return true if valid, false otherwise
     */
    private boolean validateForm() {
        // Check required fields
        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (phoneField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your phone number.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String selectedDept = (String) departmentComboBox.getSelectedItem();
        if (selectedDept == null || selectedDept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a department to visit.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (purposeArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe the purpose of your visit.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Clears the form fields
     */
    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        departmentComboBox.setSelectedIndex(0);
        staffComboBox.removeAllItems();
        staffComboBox.setEnabled(false);
        purposeArea.setText("");
    }
    
    /**
     * Main method to run the visitor reporting panel standalone
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
                    VisitorReportingPanel frame = new VisitorReportingPanel();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}