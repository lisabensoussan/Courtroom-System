package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import control.Court;
import exceptions.*;
import model.*;

/**
 * Panel for managing courtrooms
 */
public class CourtroomManagementEmployee extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable courtroomTable;
    private DefaultTableModel tableModel;
    private Court court;
    private JTextField searchField;
    private JButton addButton, updateButton, deleteButton, clearButton;

    /**
     * Create the panel for courtroom viewing
     */
    public CourtroomManagementEmployee() {
        court = Main.court;
        
        setTitle("Courtroom Management");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 806, 550);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        
        searchField = new JTextField();
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        contentPane.add(searchPanel, BorderLayout.NORTH);
        
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
        tableModel.addColumn("Courtroom Number");
        tableModel.addColumn("Department Number");
        tableModel.addColumn("Department Name");
        
        courtroomTable = new JTable(tableModel);
        courtroomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courtroomTable.getTableHeader().setReorderingAllowed(false);
        courtroomTable.getTableHeader().setResizingAllowed(true);
        
        scrollPane.setViewportView(courtroomTable);
        
        // Load courtrooms into table
        refreshCourtroomTable();
        
        // Button Panel
     // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));

        
        addButton = createStyledButton("Add");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        clearButton = createStyledButton("Clear Form");

        addButton.addActionListener(e -> addCourtroom());
        updateButton.addActionListener(e -> updateCourtroom());
        deleteButton.addActionListener(e -> deleteCourtroom());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);


        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.revalidate();
        contentPane.repaint();

    }
    
    private void clearForm() {
        searchField.setText(""); // Clears the search field
        courtroomTable.clearSelection(); // Deselects any selected row in the table
    }

    
    private void addCourtroom() {
        try {
            String input = JOptionPane.showInputDialog(this, "Enter Courtroom Number:", "Add Courtroom", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.trim().isEmpty()) {
                return;
            }

            int courtroomNumber = Integer.parseInt(input.trim());

            // Ensure courtroom does not exist
            if (court.getRealCourtroom(courtroomNumber) != null) { 
                JOptionPane.showMessageDialog(this, "Courtroom already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ask for Department Assignment
            String departmentInput = JOptionPane.showInputDialog(this, "Enter Department Number (or leave empty):", "Assign Department", JOptionPane.PLAIN_MESSAGE);
            Department department = null;
            
            if (departmentInput != null && !departmentInput.trim().isEmpty()) {
                int departmentNumber = Integer.parseInt(departmentInput.trim());
                department = court.getAllDepartments().get(departmentNumber);

                if (department == null) {
                    JOptionPane.showMessageDialog(this, "Invalid department number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Create and add the courtroom
            Courtroom newCourtroom = new Courtroom(courtroomNumber, department);

            if (court.addCourtroom(newCourtroom)) {
                JOptionPane.showMessageDialog(this, "Courtroom added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCourtroomTable();
                Main.save();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add courtroom.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        Main.save();
    }


    private void updateCourtroom() {
        int selectedRow = courtroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a courtroom to update.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courtroomNumber = (int) tableModel.getValueAt(selectedRow, 0);

        String newDepartmentInput = JOptionPane.showInputDialog(this, "Enter new Department Number:", "Update Courtroom", JOptionPane.PLAIN_MESSAGE);
        if (newDepartmentInput == null || newDepartmentInput.trim().isEmpty()) {
            return;
        }

        try {
            int departmentNumber = Integer.parseInt(newDepartmentInput.trim());
            Department department = court.getAllDepartments().get(departmentNumber);

            if (department == null) {
                JOptionPane.showMessageDialog(this, "Department not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Courtroom courtroom = court.getAllCourtrooms().get(courtroomNumber);
            courtroom.setDepartment(department);

            JOptionPane.showMessageDialog(this, "Courtroom updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCourtroomTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourtroom() {
        int selectedRow = courtroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a courtroom to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courtroomNumber = (int) tableModel.getValueAt(selectedRow, 0);
        Courtroom courtroom = court.getAllCourtrooms().get(courtroomNumber);

        if (courtroom == null) {
            JOptionPane.showMessageDialog(this, "Courtroom not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this courtroom?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Call the existing method in Court class
            boolean success = court.removeCourtroom(courtroom);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Courtroom deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCourtroomTable(); // Refresh the table after deletion
                Main.save(); // Save changes
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting courtroom. It may be associated with a department.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void loadCourtroomData() {
        tableModel.setRowCount(0);
        for (Courtroom courtroom : court.getAllCourtrooms().values()) {
            Department department = courtroom.getDepartment();
            tableModel.addRow(new Object[]{
                courtroom.getCourtroomNumber(),
                department != null ? department.getNumber() : "",
                department != null ? department.getName() : "Not Assigned"
            });
        }
    }

    private void populateForm() {
        int selectedRow = courtroomTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int courtroomNumber = (int) tableModel.getValueAt(selectedRow, 0);
        int departmentNumber = (tableModel.getValueAt(selectedRow, 1) != "") ? (int) tableModel.getValueAt(selectedRow, 1) : -1;

        searchField.setText(String.valueOf(courtroomNumber));

        if (departmentNumber != -1) {
            searchField.setText(searchField.getText() + " | Department: " + departmentNumber);
        }
    }

    
    /**
     * Refreshes the courtroom table with current data
     */
    private void refreshCourtroomTable() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Add all courtrooms to the table
        for (Courtroom courtroom : court.getAllCourtrooms().values()) {
            Department department = courtroom.getDepartment();
            tableModel.addRow(new Object[] {
                courtroom.getCourtroomNumber(),
                department != null ? department.getNumber() : "",
                department != null ? department.getName() : "Not Assigned"
            });
        }
    }
    
    /**
     * Filters the table based on search input
     */
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        DefaultTableModel filteredModel = new DefaultTableModel();
        
        // Define table columns
        filteredModel.addColumn("Courtroom Number");
        filteredModel.addColumn("Department Number");
        filteredModel.addColumn("Department Name");
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String courtroomNumber = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String departmentNumber = tableModel.getValueAt(i, 1).toString().toLowerCase();
            String departmentName = tableModel.getValueAt(i, 2).toString().toLowerCase();
            
            if (courtroomNumber.contains(searchText) || departmentNumber.contains(searchText) || departmentName.contains(searchText)) {
                filteredModel.addRow(new Object[]{
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2)
                });
            }
        }
        
        courtroomTable.setModel(filteredModel);
    }
    
    /**
     * Creates a styled button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(new Color(35, 63, 134));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        return button;
    }
}

