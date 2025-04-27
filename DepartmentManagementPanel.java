package view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import control.Court;
import model.Department;
import enums.Specialization;

/**
 * Panel for viewing departments (no editing allowed)
 */
public class DepartmentManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private Court court;
    private JTextField searchField;

    /**
     * Create the panel for department viewing
     */
    public DepartmentManagementPanel() {
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
        tableModel.addColumn("Number");
        tableModel.addColumn("Name");
        tableModel.addColumn("Manager");
        tableModel.addColumn("Building");
        tableModel.addColumn("Specialization");
        
        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.getTableHeader().setReorderingAllowed(false);
        departmentTable.getTableHeader().setResizingAllowed(true);
        
        scrollPane.setViewportView(departmentTable);
        
        // Load departments into table
        refreshDepartmentTable();
    }
    
    /**
     * Refreshes the department table with current data
     */
    private void refreshDepartmentTable() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Add all departments to the table
        for (Department department : court.getAllDepartments().values()) {
            tableModel.addRow(new Object[] {
                department.getNumber(),
                department.getName(),
                department.getManager() != null ? department.getManager().getFirstName() + " " + department.getManager().getLastName() : "Not Assigned",
                department.getBuilding(),
                department.getSpecialization()
            });
        }
    }
    
    /**
     * Filters the department table based on search input
     */
    private void filterDepartmentTable(String query) {
        tableModel.setRowCount(0);
        
        for (Department department : court.getAllDepartments().values()) {
            if (department.getName().toLowerCase().contains(query.toLowerCase()) ||
                String.valueOf(department.getNumber()).contains(query) ||
                department.getBuilding().toLowerCase().contains(query.toLowerCase()) ||
                (department.getManager() != null && (department.getManager().getFirstName().toLowerCase().contains(query.toLowerCase()) || department.getManager().getLastName().toLowerCase().contains(query.toLowerCase()))) ||
                department.getSpecialization().toString().toLowerCase().contains(query.toLowerCase())) {
                
                tableModel.addRow(new Object[] {
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
