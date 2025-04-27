package view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import control.Court;
import enums.Specialization;
import model.*;

/**
 * Public view for searching and viewing case verdicts
 * This is an additional feature - public access without login
 */
public class PublicVerdictView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable verdictTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JTextArea verdictDetailsArea;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Create the frame for public verdict view.
     */
    public PublicVerdictView() {
        this.court = Main.court;
        
        setTitle("HRS Court Management System - Public Verdict Access");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);
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
        
        JLabel titleLabel = new JLabel("Public Verdict Search");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(new TitledBorder(null, "Search Criteria", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPane.add(searchPanel, BorderLayout.NORTH);
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JLabel lblCategory = new JLabel("Category:");
        searchPanel.add(lblCategory);
        
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "All Cases", "Criminal Cases", "Financial Cases", "Family Cases"
        }));
        searchPanel.add(categoryComboBox);
        
        JLabel lblSearch = new JLabel("Search:");
        searchPanel.add(lblSearch);
        
        searchField = new JTextField();
        searchPanel.add(searchField);
        searchField.setColumns(20);
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(new Color(49, 67, 130));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchVerdicts();
            }
        });
        searchPanel.add(searchButton);
        
        JButton resetButton = new JButton("Reset");
        resetButton.setForeground(new Color(35, 63, 134));
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetSearch();
            }
        });
        searchPanel.add(resetButton);
        
        // Create split pane for table and details
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Create table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout(0, 0));
        splitPane.setTopComponent(tablePanel);
        
        JScrollPane scrollPane = new JScrollPane();
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create verdict table
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Define table columns
        tableModel.addColumn("Case Code");
        tableModel.addColumn("Case Type");
        tableModel.addColumn("Verdict Date");
        tableModel.addColumn("Judge");
        tableModel.addColumn("Summary");
        
        verdictTable = new JTable(tableModel);
        verdictTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        verdictTable.getTableHeader().setReorderingAllowed(false);
        
        // Add row sorter for filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        verdictTable.setRowSorter(sorter);
        
        scrollPane.setViewportView(verdictTable);
        
        // Add selection listener to show details when a row is selected
        verdictTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && verdictTable.getSelectedRow() != -1) {
                displayVerdictDetails();
            }
        });
        
        // Create details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setBorder(new TitledBorder(null, "Verdict Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        detailsPanel.setLayout(new BorderLayout(0, 0));
        splitPane.setBottomComponent(detailsPanel);
        
        verdictDetailsArea = new JTextArea();
        verdictDetailsArea.setEditable(false);
        verdictDetailsArea.setLineWrap(true);
        verdictDetailsArea.setWrapStyleWord(true);
        verdictDetailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane detailsScrollPane = new JScrollPane(verdictDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Button panel for downloading
        JPanel buttonPanel = new JPanel();
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton downloadButton = new JButton("Download Verdict");
        downloadButton.setBackground(new Color(46, 204, 113));
        downloadButton.setForeground(new Color(52, 75, 164));
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadVerdict();
            }
        });
        buttonPanel.add(downloadButton);
        
        JButton backButton = new JButton("Back to Login");
        backButton.setForeground(new Color(35, 63, 134));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LOGIN().setVisible(true);
            }
        });
        buttonPanel.add(backButton);
        
        // Load initial verdicts
        loadVerdicts();
    }
    
    /**
     * Loads all verdicts with finished cases into the table
     */
    private void loadVerdicts() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Add verdicts for cases with status Finished
        for (Verdict verdict : court.getAllVerdicts().values()) {
            Case caseObj = verdict.getCasee();
            
            // Only show verdicts for finished cases
            if (caseObj != null && caseObj.getCaseStatus() == enums.Status.finished) {
                tableModel.addRow(new Object[] {
                    caseObj.getCode(),
                    caseObj.getCaseType().toString(),
                    dateFormat.format(verdict.getIssusedDate()),
                    verdict.getJudge().getFirstName() + " " + verdict.getJudge().getLastName(),
                    verdict.getVerdictSummary().length() > 50 
                        ? verdict.getVerdictSummary().substring(0, 50) + "..." 
                        : verdict.getVerdictSummary()
                });
            }
        }
    }
    
    /**
     * Searches verdicts based on criteria
     */
    private void searchVerdicts() {
        // Get search text
        String searchText = searchField.getText().toLowerCase().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Add matching verdicts
        for (Verdict verdict : court.getAllVerdicts().values()) {
            Case caseObj = verdict.getCasee();
            
            // Skip non-finished cases
            if (caseObj == null || caseObj.getCaseStatus() != enums.Status.finished) {
                continue;
            }
            
            // Filter by case type if category is selected
            if (!"All Cases".equals(category)) {
                boolean matchesCategory = false;
                
                if ("Criminal Cases".equals(category) && caseObj instanceof CriminalCase) {
                    matchesCategory = true;
                } else if ("Financial Cases".equals(category) && caseObj instanceof FinancialCase) {
                    matchesCategory = true;
                } else if ("Family Cases".equals(category) && caseObj instanceof FamilyCase) {
                    matchesCategory = true;
                }
                
                if (!matchesCategory) {
                    continue;
                }
            }
            
            // Filter by search text if provided
            if (!searchText.isEmpty()) {
                // Search in multiple fields
                boolean matchesSearch = 
                    caseObj.getCode().toLowerCase().contains(searchText) ||
                    verdict.getVerdictSummary().toLowerCase().contains(searchText) ||
                    verdict.getJudge().getFirstName().toLowerCase().contains(searchText) ||
                    verdict.getJudge().getLastName().toLowerCase().contains(searchText) ||
                    caseObj.getAccused().getFirstName().toLowerCase().contains(searchText) ||
                    caseObj.getAccused().getLastName().toLowerCase().contains(searchText);
                
                if (!matchesSearch) {
                    continue;
                }
            }
            
            // Add to table if passed all filters
            tableModel.addRow(new Object[] {
                caseObj.getCode(),
                caseObj.getCaseType().toString(),
                dateFormat.format(verdict.getIssusedDate()),
                verdict.getJudge().getFirstName() + " " + verdict.getJudge().getLastName(),
                verdict.getVerdictSummary().length() > 50 
                    ? verdict.getVerdictSummary().substring(0, 50) + "..." 
                    : verdict.getVerdictSummary()
            });
        }
        
        // Show message if no results
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No verdicts found matching your criteria.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Resets search criteria and loads all verdicts
     */
    private void resetSearch() {
        searchField.setText("");
        categoryComboBox.setSelectedIndex(0);
        loadVerdicts();
        verdictDetailsArea.setText("");
    }
    
    /**
     * Displays details of the selected verdict
     */
    private void displayVerdictDetails() {
        int selectedRow = verdictTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = verdictTable.convertRowIndexToModel(selectedRow);
            String caseCode = (String) tableModel.getValueAt(modelRow, 0);
            
            // Find the case and verdict
            Case selectedCase = court.getRealCase(caseCode);
            Verdict selectedVerdict = null;
            
            if (selectedCase != null) {
                selectedVerdict = selectedCase.getVerdict();
            }
            
            if (selectedVerdict != null) {
                // Format the verdict details
                StringBuilder details = new StringBuilder();
                details.append("CASE INFORMATION\n");
                details.append("==============================================================\n");
                details.append("Case Code: ").append(selectedCase.getCode()).append("\n");
                details.append("Case Type: ").append(selectedCase.getCaseType()).append("\n");
                details.append("Case Status: ").append(selectedCase.getCaseStatus()).append("\n");
                details.append("Opening Date: ").append(dateFormat.format(selectedCase.getOpenedDate())).append("\n");
                details.append("\n");
                
                details.append("ACCUSED INFORMATION\n");
                details.append("==============================================================\n");
                details.append("Name: ").append(selectedCase.getAccused().getFirstName())
                       .append(" ").append(selectedCase.getAccused().getLastName()).append("\n");
                details.append("Job: ").append(selectedCase.getAccused().getJob()).append("\n");
                details.append("\n");
                
                details.append("VERDICT INFORMATION\n");
                details.append("==============================================================\n");
                details.append("Verdict ID: ").append(selectedVerdict.getVerdictID()).append("\n");
                details.append("Date Issued: ").append(dateFormat.format(selectedVerdict.getIssusedDate())).append("\n");
                details.append("Judge: ").append(selectedVerdict.getJudge().getFirstName())
                       .append(" ").append(selectedVerdict.getJudge().getLastName()).append("\n");
                details.append("\n");
                details.append("VERDICT SUMMARY\n");
                details.append("==============================================================\n");
                details.append(selectedVerdict.getVerdictSummary());
                
                // Add case-specific information
                details.append("\n\n");
                details.append("CASE-SPECIFIC DETAILS\n");
                details.append("==============================================================\n");
                
                if (selectedCase instanceof CriminalCase) {
                    CriminalCase criminalCase = (CriminalCase) selectedCase;
                    details.append("Crime Scene: ").append(criminalCase.getCrimeScene()).append("\n");
                    details.append("Crime Tool: ").append(criminalCase.getCrimeTool()).append("\n");
                    if (criminalCase.getVictim() != null) {
                        details.append("Victim: ").append(criminalCase.getVictim().getFirstName())
                               .append(" ").append(criminalCase.getVictim().getLastName()).append("\n");
                    }
                } else if (selectedCase instanceof FinancialCase) {
                    FinancialCase financialCase = (FinancialCase) selectedCase;
                    details.append("Losses Amount: ").append(financialCase.getLossesAmount()).append("\n");
                    details.append("Damaged Item: ").append(financialCase.getDamagedItem()).append("\n");
                } else if (selectedCase instanceof FamilyCase) {
                    FamilyCase familyCase = (FamilyCase) selectedCase;
                    details.append("Relation Type: ").append(familyCase.getRelationType()).append("\n");
                    if (familyCase.getVictim() != null) {
                        details.append("Victim: ").append(familyCase.getVictim().getFirstName())
                               .append(" ").append(familyCase.getVictim().getLastName()).append("\n");
                    }
                }
                
                // Show testimony count
                details.append("\nNumber of Testimonies: ").append(selectedCase.gettestimoniesList().size());
                
                // Display in the text area
                verdictDetailsArea.setText(details.toString());
                verdictDetailsArea.setCaretPosition(0); // Scroll to top
            }
        }
    }
    
    /**
     * Downloads the selected verdict as a text file
     */
    private void downloadVerdict() {
        int selectedRow = verdictTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a verdict to download.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get case code from selected row
            int modelRow = verdictTable.convertRowIndexToModel(selectedRow);
            String caseCode = (String) tableModel.getValueAt(modelRow, 0);
            
            // Show file save dialog
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Verdict");
            fileChooser.setSelectedFile(new File("Verdict_" + caseCode + ".txt"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                
                // Ensure it has .txt extension
                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                }
                
                // Write verdict details to the file
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(verdictDetailsArea.getText());
                }
                
                JOptionPane.showMessageDialog(this, "Verdict downloaded successfully to: " 
                        + fileToSave.getAbsolutePath(), "Download Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving verdict: " + e.getMessage(), 
                    "Download Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run the public verdict view standalone
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
                    PublicVerdictView frame = new PublicVerdictView();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}