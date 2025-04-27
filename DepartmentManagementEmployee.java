package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import control.Court;
import model.Department;
import enums.Specialization;

/**
 * Panel for managing departments
 */
public class DepartmentManagementEmployee extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private Court court;
    private JTextField searchField;
    private JButton addButton, updateButton, deleteButton, clearButton;

    /**
     * Create the panel for department management
     */
    public DepartmentManagementEmployee() {
        court = Main.court;
        
        setTitle("Department Management");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 800, 500);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(new TitledBorder("Search Department"));
        contentPane.add(searchPanel, BorderLayout.NORTH);
        
        searchField = new JTextField(20);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterDepartmentTable(searchField.getText());
            }
        });
     // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));

        addButton = createStyledButton("Add");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        clearButton = createStyledButton("Clear Form");

        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Add button panel at the bottom of the UI
        contentPane.add(buttonPanel, BorderLayout.SOUTH);


        // Table Panel
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("Number");
        tableModel.addColumn("Name");
        tableModel.addColumn("Specialization");
        
        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.getTableHeader().setReorderingAllowed(false);
        departmentTable.getTableHeader().setResizingAllowed(true);
        
        // ADD Selection Listener
        departmentTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                populateForm(); // Call populateForm() on selection
            }
        });
        
        scrollPane.setViewportView(departmentTable);
        
        // Load departments into table
        loadDepartmentData();
    }


    private void clearForm() {
        searchField.setText("");
        departmentTable.clearSelection();
    }

    private void addDepartment() {
        try {
            String input = JOptionPane.showInputDialog(this, "Enter Department Number:", "Add Department", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.trim().isEmpty()) {
                return;
            }

            int departmentNumber = Integer.parseInt(input.trim());

            // Check if the department already exists
            if (court.getRealDepartment(departmentNumber) != null) { 
                JOptionPane.showMessageDialog(this, "Department already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String departmentName = JOptionPane.showInputDialog(this, "Enter Department Name:");
            if (departmentName == null || departmentName.trim().isEmpty()) {
                return;
            }

            Specialization specialization = (Specialization) JOptionPane.showInputDialog(
                this, 
                "Select Specialization:", 
                "Department Specialization", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                Specialization.values(), 
                Specialization.values()[0]
            );

            if (specialization == null) {
                return;
            }

            // Create new department
            Department newDepartment = new Department(departmentNumber, departmentName, null, "", specialization);
            
            // Use the proper method to add the department
            if (court.addDepartment(newDepartment)) {
                JOptionPane.showMessageDialog(this, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDepartmentTable();
                Main.save();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding department.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        Main.save();
    }



    private void updateDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to update.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int departmentNumber = (int) tableModel.getValueAt(selectedRow, 0);

        String newDepartmentName = JOptionPane.showInputDialog(this, "Enter new Department Name:", tableModel.getValueAt(selectedRow, 1));
        if (newDepartmentName == null || newDepartmentName.trim().isEmpty()) {
            return;
        }

        Specialization specialization = (Specialization) JOptionPane.showInputDialog(this, "Select Specialization:", 
                "Department Specialization", JOptionPane.QUESTION_MESSAGE, null, Specialization.values(), tableModel.getValueAt(selectedRow, 2));

        if (specialization == null) {
            return;
        }

        Department department = court.getAllDepartments().get(departmentNumber);
        department.setName(newDepartmentName);
        department.setSpecialization(specialization);

        JOptionPane.showMessageDialog(this, "Department updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshDepartmentTable();
        Main.save();
    }

    
    private void deleteDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int departmentNumber = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this department?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Use the proper method to remove the department
            Department department = court.getAllDepartments().get(departmentNumber);
            if (department != null) {
                court.getAllDepartments().remove(departmentNumber);
                JOptionPane.showMessageDialog(this, "Department deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDepartmentTable();
                Main.save();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Department not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        Main.save();
    }


    private void loadDepartmentData() {
        tableModel.setRowCount(0);
        for (Department department : court.getAllDepartments().values()) {
            tableModel.addRow(new Object[]{
                department.getNumber(),
                department.getName(),
                department.getSpecialization()
            });
        }
    }

    private void populateForm() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        searchField.setText(tableModel.getValueAt(selectedRow, 0).toString() + " | " + tableModel.getValueAt(selectedRow, 1).toString());
    }


    private void refreshDepartmentTable() {
        tableModel.setRowCount(0); // Clear the table before refreshing

        for (Department department : court.getAllDepartments().values()) {
            tableModel.addRow(new Object[]{
                department.getNumber(),
                department.getName(),
                department.getSpecialization()  // ✅ Now Specialization is correctly added!
            });
        }

        departmentTable.repaint();
        departmentTable.revalidate();
    }



    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        DefaultTableModel filteredModel = new DefaultTableModel();
        
        filteredModel.addColumn("Department Number");
        filteredModel.addColumn("Department Name");
        filteredModel.addColumn("Specialization");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String departmentNumber = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String departmentName = tableModel.getValueAt(i, 1).toString().toLowerCase();
            String specialization = tableModel.getValueAt(i, 2).toString().toLowerCase();

            if (departmentNumber.contains(searchText) || departmentName.contains(searchText) || specialization.contains(searchText)) {
                filteredModel.addRow(new Object[]{
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2)
                });
            }
        }
        departmentTable.setModel(filteredModel);
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
    
    private void filterDepartmentTable(String query) {
        tableModel.setRowCount(0); // Clear existing table data

        for (Department department : court.getAllDepartments().values()) {
            // Convert values to lowercase for case-insensitive search
            if (department.getName().toLowerCase().contains(query.toLowerCase()) ||
                String.valueOf(department.getNumber()).contains(query) ||
                department.getBuilding().toLowerCase().contains(query.toLowerCase()) ||
                (department.getManager() != null && 
                    (department.getManager().getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                     department.getManager().getLastName().toLowerCase().contains(query.toLowerCase()))) ||
                department.getSpecialization().toString().toLowerCase().contains(query.toLowerCase())) {
                
                tableModel.addRow(new Object[]{
                    department.getNumber(),
                    department.getName(),
                    department.getManager() != null ? department.getManager().getFirstName() + " " + department.getManager().getLastName() : "Not Assigned",
                    department.getBuilding(),
                    department.getSpecialization()
                });
            }
        }
    }


}
