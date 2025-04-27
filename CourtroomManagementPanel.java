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
public class CourtroomManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable courtroomTable;
    private DefaultTableModel tableModel;
    private Court court;
    private JTextField searchField;

    /**
     * Create the panel for courtroom viewing
     */
    public CourtroomManagementPanel() {
        court = Main.court;
        
        setTitle("Courtroom Management");
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
}