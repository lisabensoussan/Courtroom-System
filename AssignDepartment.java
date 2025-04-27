package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import control.Court;
import model.*;
import enums.*;

/**
 * Panel for assigning lawyers and judges to departments
 */
public class AssignDepartment extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private Court court;
    
    // UI Components
    private JComboBox<Lawyer> staffComboBox;
    private JComboBox<Department> departmentComboBox;
    private JTextArea infoTextArea;
    private JTable currentAssignmentsTable;
    private DefaultTableModel tableModel;
    
    // Colors for styled components
    private final Color BUTTON_BG_COLOR = new Color(25, 42, 86); // Dark blue
    private final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    /**
     * Create the panel for assigning staff to departments
     */
    public AssignDepartment() {
        court = Main.court;
        
        setTitle("Assign to Department");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(50, 50, 850, 600);
        
        // Create main content panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);
        
        // Create top panel with staff selection
        JPanel staffPanel = new JPanel(new GridBagLayout());
        staffPanel.setBorder(BorderFactory.createTitledBorder("Staff Selection"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Staff selection combo box
        JLabel staffLabel = new JLabel("Select Staff:");
        staffComboBox = new JComboBox<>();
        updateStaffComboBox();
        staffComboBox.addActionListener(e -> updateInfoTextArea());
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        staffPanel.add(staffLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        staffPanel.add(staffComboBox, gbc);
        
        // Info text area
        infoTextArea = new JTextArea(8, 40);
        infoTextArea.setEditable(false);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setLineWrap(true);
        infoTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        staffPanel.add(new JScrollPane(infoTextArea), gbc);
        
        // Create department selection panel
        JPanel departmentPanel = createDepartmentPanel();
        
        // Create center panel with staff selection and department selection
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        centerPanel.add(staffPanel);
        centerPanel.add(departmentPanel);
        
        contentPane.add(centerPanel, BorderLayout.CENTER);
        
        // Create bottom panel with staff info and assignment button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Current Assignments"));
        
        // Create table for current assignments
        createAssignmentsTable();
        JScrollPane tableScrollPane = new JScrollPane(currentAssignmentsTable);
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton assignButton = createStyledButton("Assign to Department");
        assignButton.addActionListener(e -> assignToDepartment());
        buttonPanel.add(assignButton);
        
        JButton removeButton = createStyledButton("Remove from Department");
        removeButton.addActionListener(e -> removeFromDepartment());
        buttonPanel.add(removeButton);
        
        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> refreshAssignments());
        buttonPanel.add(refreshButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize UI
        updateInfoTextArea();
        refreshAssignments();
    }
    /**
     * Updates the staff combo box with both lawyers and judges
     */
    private void updateStaffComboBox() {
        staffComboBox.removeAllItems();
        
        ArrayList<Lawyer> staffList = new ArrayList<>();
        
        // Add all lawyers and judges
        for (Lawyer staff : court.getAllLawyers().values()) {
            staffList.add(staff);
        }
        
        // Sort by ID for easier navigation
        Collections.sort(staffList, Comparator.comparing(Lawyer::getId));
        
        // Add sorted staff to combo box
        for (Lawyer staff : staffList) {
            staffComboBox.addItem(staff);
        }
        
        // Custom renderer for staff display
        staffComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Lawyer) {
                    Lawyer staff = (Lawyer) value;
                    String type = (staff instanceof Judge) ? "Judge" : "Lawyer";
                    value = staff.getId() + " - " + staff.getFirstName() + " " + staff.getLastName() + 
                           " (" + type + ", " + staff.getSpecialization() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }
    
    /**
     * Creates the department selection panel
     */
    private JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Department Selection"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Department selection combobox
        JLabel departmentLabel = new JLabel("Select Department:");
        departmentComboBox = new JComboBox<>();
        updateDepartmentComboBox();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(departmentLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(departmentComboBox, gbc);
        
        // Department info
        JTextArea departmentInfoArea = new JTextArea(5, 40);
        departmentInfoArea.setEditable(false);
        departmentInfoArea.setWrapStyleWord(true);
        departmentInfoArea.setLineWrap(true);
        departmentInfoArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Update department info when selection changes
        departmentComboBox.addActionListener(e -> {
            Department dept = (Department) departmentComboBox.getSelectedItem();
            if (dept != null) {
                StringBuilder info = new StringBuilder();
                info.append("Department Number: ").append(dept.getNumber()).append("\n");
                info.append("Name: ").append(dept.getName()).append("\n");
                info.append("Building: ").append(dept.getBuilding()).append("\n");
                info.append("Specialization: ").append(dept.getSpecialization()).append("\n");
                info.append("Manager: ").append(dept.getManager() != null ? 
                        dept.getManager().getFirstName() + " " + dept.getManager().getLastName() : "None").append("\n");
                info.append("Number of Employees: ").append(dept.getEmployees().size()).append("\n");
                info.append("Number of Lawyers: ").append(dept.getLawyers().size()).append("\n");
                departmentInfoArea.setText(info.toString());
            } else {
                departmentInfoArea.setText("");
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(departmentInfoArea), gbc);
        
        return panel;
    }
    
    /**
     * Creates the table for displaying current assignments
     */
    private void createAssignmentsTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Specialization");
        tableModel.addColumn("Department");
        
        currentAssignmentsTable = new JTable(tableModel);
        currentAssignmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        currentAssignmentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener to update form when row is selected
        currentAssignmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateFormFromSelection();
            }
        });
    }
    
    
    /**
     * Updates the info text area based on selected staff
     */
    private void updateInfoTextArea() {
        Lawyer staff = (Lawyer) staffComboBox.getSelectedItem();
        if (staff == null) {
            infoTextArea.setText("No staff member selected.");
            return;
        }
        
        if (staff instanceof Judge) {
            displayJudgeInfo((Judge) staff);
        } else {
            displayLawyerInfo(staff);
        }
    }
    
    /**
     * Updates the department combo box with current data
     */
    private void updateDepartmentComboBox() {
        departmentComboBox.removeAllItems();
        
        for (Department department : court.getAllDepartments().values()) {
            departmentComboBox.addItem(department);
        }
        
        // Custom renderer for department display
        departmentComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Department) {
                    Department dept = (Department) value;
                    value = dept.getNumber() + ": " + dept.getName() + " (" + dept.getSpecialization() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }
    
    /**
     * Handles selection changes in the staff table
     */
    private void updateFormFromSelection() {
        int selectedRow = currentAssignmentsTable.getSelectedRow();
        if (selectedRow < 0) return;
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Find the staff in the combo box and select it
        for (int i = 0; i < staffComboBox.getItemCount(); i++) {
            Lawyer staff = staffComboBox.getItemAt(i);
            if (staff.getId() == id) {
                staffComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        // Update department selection if there is a department
        String departmentName = (String) tableModel.getValueAt(selectedRow, 4);
        if (departmentName != null && !departmentName.equals("None")) {
            for (int i = 0; i < departmentComboBox.getItemCount(); i++) {
                Department dept = (Department) departmentComboBox.getItemAt(i);
                if (dept.getName().equals(departmentName)) {
                    departmentComboBox.setSelectedItem(dept);
                    break;
                }
            }
        }
    }
    
    /**
     * Displays lawyer information in the text area
     */
    private void displayLawyerInfo(Lawyer lawyer) {
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(lawyer.getId()).append("\n");
        info.append("Name: ").append(lawyer.getFirstName()).append(" ").append(lawyer.getLastName()).append("\n");
        info.append("Gender: ").append(lawyer.getGender()).append("\n");
        info.append("Phone: ").append(lawyer.getPhoneNumber()).append("\n");
        info.append("Email: ").append(lawyer.getEmail()).append("\n");
        info.append("Specialization: ").append(lawyer.getSpecialization()).append("\n");
        info.append("License Number: ").append(lawyer.getLicenseNumber()).append("\n");
        info.append("Salary: $").append(String.format("%.2f", lawyer.getSalary())).append("\n");
        info.append("Current Department: ").append(lawyer.getDepartment() != null ? 
                lawyer.getDepartment().getName() : "None").append("\n");
        info.append("Number of Cases: ").append(lawyer.getCasesHandled().size()).append("\n");
        
        infoTextArea.setText(info.toString());
        infoTextArea.setCaretPosition(0);
    }
    
    /**
     * Displays judge information in the text area
     */
    private void displayJudgeInfo(Judge judge) {
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(judge.getId()).append("\n");
        info.append("Name: ").append(judge.getFirstName()).append(" ").append(judge.getLastName()).append("\n");
        info.append("Gender: ").append(judge.getGender()).append("\n");
        info.append("Phone: ").append(judge.getPhoneNumber()).append("\n");
        info.append("Email: ").append(judge.getEmail()).append("\n");
        info.append("Specialization: ").append(judge.getSpecialization()).append("\n");
        info.append("License Number: ").append(judge.getLicenseNumber()).append("\n");
        info.append("Experience Years: ").append(judge.getExperienceYear()).append("\n");
        info.append("Salary: $").append(String.format("%.2f", judge.getSalary())).append("\n");
        info.append("Current Department: ").append(judge.getDepartment() != null ? 
                judge.getDepartment().getName() : "None").append("\n");
        info.append("Number of Cases Presided: ").append(judge.getCasesPresided().size()).append("\n");
        
        infoTextArea.setText(info.toString());
        infoTextArea.setCaretPosition(0);
    }
    
    /**
     * Assigns the selected staff to the selected department
     */
    private void assignToDepartment() {
        Lawyer staff = (Lawyer) staffComboBox.getSelectedItem();
        Department department = (Department) departmentComboBox.getSelectedItem();
        
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Please select a staff member.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (department == null) {
            JOptionPane.showMessageDialog(this, "Please select a department.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = false;
        
        if (staff instanceof Judge) {
            success = court.addJudgeToDepartment(department, (Judge) staff);
        } else {
            success = court.addLawyerToDepartment(department, staff);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Successfully assigned to department.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Save changes
            Main.save();
            // Refresh the display
            refreshAssignments();
            updateInfoTextArea();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign to department.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Removes the selected staff from their department
     */
    private void removeFromDepartment() {
        int selectedRow = currentAssignmentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String departmentName = (String) tableModel.getValueAt(selectedRow, 4);
        
        if (departmentName.equals("None")) {
            JOptionPane.showMessageDialog(this, "This staff member is not assigned to any department.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove this staff member from their department?", 
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = false;
        
        if ("Lawyer".equals(type)) {
            Lawyer lawyer = court.getRealLawyer(id);
            if (lawyer != null && lawyer.getDepartment() != null) {
                Department dept = lawyer.getDepartment();
                success = dept.removeLawyer(lawyer);
            }
        } else if ("Judge".equals(type)) {
            Judge judge = court.getRealJudge(id);
            if (judge != null && judge.getDepartment() != null) {
                Department dept = judge.getDepartment();
                success = dept.removeJudge(judge);
            }
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Successfully removed from department.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Save changes
            Main.save();
            // Refresh the display
            refreshAssignments();
            updateInfoTextArea();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to remove from department.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Refreshes the assignments table with current data
     */
    private void refreshAssignments() {
        tableModel.setRowCount(0);
        
        // Add all lawyers and judges to the table
        for (Lawyer staff : court.getAllLawyers().values()) {
            String type = (staff instanceof Judge) ? "Judge" : "Lawyer";
            String departmentName = (staff.getDepartment() != null) ? staff.getDepartment().getName() : "None";
            
            tableModel.addRow(new Object[] {
                staff.getId(),
                staff.getFirstName() + " " + staff.getLastName(),
                type,
                staff.getSpecialization(),
                departmentName
            });
        }
    }
    
    /**
     * Creates a styled button with dark blue background
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BG_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(true); // Changed to true
        button.setOpaque(true); // Add this line
        
        // This is crucial to make the background color show on some look and feels
        button.setContentAreaFilled(true);
        
        // Create a custom border with blue color
        button.setBorder(BorderFactory.createLineBorder(new Color(20, 37, 81), 1));
        
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Slightly lighter shade when hovering
                button.setBackground(new Color(35, 52, 96));
                button.setBorderPainted(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BG_COLOR);
                button.setBorderPainted(true);
            }
        });
        
        return button;
    }
}