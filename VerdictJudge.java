package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import control.Court;
import model.*;

/**
 * This class provides an interface for judges to view and manage all verdicts
 * associated with their cases.
 */
public class VerdictJudge extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    
    private Court court;
    private Judge judge;
    
    // UI Components
    private JTable verdictTable;
    private DefaultTableModel tableModel;
    private JTextField caseField, accusedField, dateField, statusField, appealField;
    private JTextArea verdictSummaryArea;
    private JButton printButton, exportButton, refreshButton;
    private JComboBox<String> filterCombo;
    private JTextField searchField;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Create the internal frame.
     */
    public VerdictJudge(Judge judge) {
        super("Verdicts Management", true, true, true, true);
        this.judge = judge;
        this.court = Main.court;
        
        // Initialize UI
        setBounds(50, 50, 800, 600);
        
        // Create main content panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 5));
        setContentPane(contentPane);
        
        // Create toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        toolbarPanel.add(refreshButton);
        
        // Print verdict button
        printButton = new JButton("Print Verdict");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printVerdict();
            }
        });
        toolbarPanel.add(printButton);
        
        // Export verdict button
        exportButton = new JButton("Export Verdict");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportVerdict();
            }
        });
        toolbarPanel.add(exportButton);
        
        // Filter label and combo
        toolbarPanel.add(new JLabel("Filter by:"));
        
        filterCombo = new JComboBox<>(new String[] {
            "All Verdicts", "With Appeals", "No Appeals", "Recent Verdicts"
        });
        filterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterVerdicts();
            }
        });
        toolbarPanel.add(filterCombo);
        
        // Search label and field
        toolbarPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterVerdicts();
            }
        });
        toolbarPanel.add(searchField);
        
        contentPane.add(toolbarPanel, BorderLayout.NORTH);
        
        // Create table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add columns
        tableModel.addColumn("Verdict ID");
        tableModel.addColumn("Case Code");
        tableModel.addColumn("Case Type");
        tableModel.addColumn("Accused");
        tableModel.addColumn("Issued Date");
        tableModel.addColumn("Appeal Status");
        
        // Create table
        verdictTable = new JTable(tableModel);
        verdictTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        verdictTable.setRowHeight(25);
        verdictTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener
        verdictTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedVerdict();
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(verdictTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Verdict Details"));
        
        // Top panel for basic info
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Case info
        infoPanel.add(new JLabel("Case:"));
        caseField = new JTextField();
        caseField.setEditable(false);
        infoPanel.add(caseField);
        
        // Accused info
        infoPanel.add(new JLabel("Accused:"));
        accusedField = new JTextField();
        accusedField.setEditable(false);
        infoPanel.add(accusedField);
        
        // Issue date
        infoPanel.add(new JLabel("Issued Date:"));
        dateField = new JTextField();
        dateField.setEditable(false);
        infoPanel.add(dateField);
        
        // Case status
        infoPanel.add(new JLabel("Case Status:"));
        statusField = new JTextField();
        statusField.setEditable(false);
        infoPanel.add(statusField);
        
        // Appeal status
        infoPanel.add(new JLabel("Appeal Status:"));
        appealField = new JTextField();
        appealField.setEditable(false);
        infoPanel.add(appealField);
        
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Verdict summary area
        JPanel summaryPanel = new JPanel(new BorderLayout(5, 5));
        summaryPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        summaryPanel.add(new JLabel("Verdict Summary:"), BorderLayout.NORTH);
        
        verdictSummaryArea = new JTextArea(5, 20);
        verdictSummaryArea.setEditable(false);
        verdictSummaryArea.setLineWrap(true);
        verdictSummaryArea.setWrapStyleWord(true);
        
        JScrollPane summaryScrollPane = new JScrollPane(verdictSummaryArea);
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);
        
        detailsPanel.add(summaryPanel, BorderLayout.CENTER);
        
        contentPane.add(detailsPanel, BorderLayout.SOUTH);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Total verdicts: 0");
        statusBar.add(statusLabel);
        
        // Load data
        loadVerdictData();
    }
    
    /**
     * Loads all verdicts data into the table
     */
    private void loadVerdictData() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            int verdictCount = 0;
            
            // Add verdicts from cases handled by this judge
            for (Case c : judge.getCasesHandled()) {
                if (c.getVerdict() != null) {
                    Verdict v = c.getVerdict();
                    tableModel.addRow(new Object[]{
                        v.getVerdictID(),
                        c.getCode(),
                        c.getCaseType(),
                        c.getAccused().getFirstName() + " " + c.getAccused().getLastName(),
                        dateFormat.format(v.getIssusedDate()),
                        v.getAppeal() != null ? "Under Appeal" : "No Appeal"
                    });
                    verdictCount++;
                }
            }
            
            // Add verdicts from cases presided over by this judge
            for (Case c : judge.getCasesPresided()) {
                if (c.getVerdict() != null) {
                    // Check if this verdict is already in the table (to avoid duplicates)
                    boolean alreadyAdded = false;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 0).equals(c.getVerdict().getVerdictID())) {
                            alreadyAdded = true;
                            break;
                        }
                    }
                    
                    if (!alreadyAdded) {
                        Verdict v = c.getVerdict();
                        tableModel.addRow(new Object[]{
                            v.getVerdictID(),
                            c.getCode(),
                            c.getCaseType(),
                            c.getAccused().getFirstName() + " " + c.getAccused().getLastName(),
                            dateFormat.format(v.getIssusedDate()),
                            v.getAppeal() != null ? "Under Appeal" : "No Appeal"
                        });
                        verdictCount++;
                    }
                }
            }
            
            // Update verdict count in status bar
            if (getContentPane().getComponentCount() > 2) {
                JPanel statusBar = (JPanel) getContentPane().getComponent(2);
                if (statusBar.getComponentCount() > 0) {
                    JLabel statusLabel = (JLabel) statusBar.getComponent(0);
                    statusLabel.setText("Total verdicts: " + verdictCount);
                }
            }
            
            // Clear details panel
            clearDetails();
            
        } catch (Exception e) {
            System.err.println("Error loading verdict data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays the selected verdict details
     */
    private void displaySelectedVerdict() {
        try {
            int selectedRow = verdictTable.getSelectedRow();
            if (selectedRow == -1) {
                clearDetails();
                return;
            }
            
            // Get verdict ID from table
            Object idObj = verdictTable.getValueAt(selectedRow, 0);
            if (idObj == null) {
                clearDetails();
                return;
            }
            
            int verdictId = -1;
            
            // Handle different types of ID (Integer or String)
            if (idObj instanceof Integer) {
                verdictId = (Integer) idObj;
            } else {
                try {
                    verdictId = Integer.parseInt(idObj.toString());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                            "Invalid verdict ID format: " + idObj, 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    clearDetails();
                    return;
                }
            }
            
            // Get verdict from court
            Verdict selectedVerdict = court.getRealVerdict(verdictId);
            
            if (selectedVerdict == null) {
                JOptionPane.showMessageDialog(this, 
                        "Cannot find the selected verdict", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                clearDetails();
                return;
            }
            
            // Get case info
            Case c = selectedVerdict.getCasee();
            
            // Fill in details
            caseField.setText(c.getCode() + " - " + c.getCaseType());
            accusedField.setText(c.getAccused().getFirstName() + " " + c.getAccused().getLastName());
            dateField.setText(dateFormat.format(selectedVerdict.getIssusedDate()));
            statusField.setText(c.getCaseStatus().toString());
            
            // Handle appeal info
            if (selectedVerdict.getAppeal() != null) {
                Appeal appeal = selectedVerdict.getAppeal();
                appealField.setText("Appeal filed on " + dateFormat.format(appeal.getAppealDate()));
            } else {
                appealField.setText("No Appeal");
            }
            
            // Display verdict summary
            verdictSummaryArea.setText(selectedVerdict.getVerdictSummary());
            verdictSummaryArea.setCaretPosition(0); // Scroll to top
            
        } catch (Exception e) {
            System.err.println("Error displaying verdict: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                    "Error displaying verdict details: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            clearDetails();
        }
    }
    
    /**
     * Clears all fields in the details panel
     */
    private void clearDetails() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                caseField.setText("");
                accusedField.setText("");
                dateField.setText("");
                statusField.setText("");
                appealField.setText("");
                verdictSummaryArea.setText("");
            }
        });
    }
    
    /**
     * Filters verdicts based on search text and filter selection
     */
    private void filterVerdicts() {
        String searchText = searchField.getText().toLowerCase();
        String filterOption = (String) filterCombo.getSelectedItem();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean showRow = true;
            
            // Apply category filter
            if (filterOption != null && !filterOption.equals("All Verdicts")) {
                String appealStatus = (String) tableModel.getValueAt(i, 5); // Appeal Status column
                if (filterOption.equals("With Appeals") && !appealStatus.contains("Under Appeal")) {
                    showRow = false;
                } else if (filterOption.equals("No Appeals") && !appealStatus.contains("No Appeal")) {
                    showRow = false;
                } else if (filterOption.equals("Recent Verdicts")) {
                    // Would need date parsing logic for recent verdicts
                }
            }
            
            // Apply text search (if still showing after category filter)
            if (showRow && !searchText.isEmpty()) {
                boolean matchFound = false;
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object value = tableModel.getValueAt(i, j);
                    if (value != null && value.toString().toLowerCase().contains(searchText)) {
                        matchFound = true;
                        break;
                    }
                }
                showRow = matchFound;
            }
            
            // Show/hide row
            // Note: This is a simplified approach without using RowFilter
            // In a real implementation, we'd use a proper RowFilter with a TableRowSorter
            verdictTable.setRowHeight(i, showRow ? 25 : 0);
        }
    }
    
    /**
     * Refreshes verdict data
     */
    private void refreshData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadVerdictData();
                JOptionPane.showMessageDialog(VerdictJudge.this, 
                        "Verdict data refreshed", 
                        "Refresh", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    /**
     * Prints the selected verdict
     */
    private void printVerdict() {
        int selectedRow = verdictTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a verdict to print", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Placeholder for printing functionality
        JOptionPane.showMessageDialog(this, 
                "Print functionality will be implemented in a future version", 
                "Print Verdict", 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Exports the selected verdict
     */
    private void exportVerdict() {
        int selectedRow = verdictTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a verdict to export", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Placeholder for export functionality
        JOptionPane.showMessageDialog(this, 
                "Export functionality will be implemented in a future version", 
                "Export Verdict", 
                JOptionPane.INFORMATION_MESSAGE);
    }
}