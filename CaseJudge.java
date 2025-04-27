package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import control.Court;
import enums.Specialization;
import enums.Status;
import model.*;
import utils.UtilsMethods;

public class CaseJudge extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable casesTable;
    private DefaultTableModel tableModel;
    private Judge judge;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Dialog components for adding/editing
    private JDialog addMeetingDialog;
    private JDialog verdictDialog;
    private JDialog updateCaseDialog;
    
    /**
     * Create the internal frame.
     */
    public CaseJudge(Judge judge) {
        super("Cases Management", true, true, true, true);
        this.judge = judge;
        this.court = Court.getInstance();
        
        setBounds(50, 50, 1000, 600);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create toolbar with buttons
        JToolBar toolBar = createToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);
        
        // Create table model
        createTableModel();
        
        // Create table with scrollpane
        casesTable = new JTable(tableModel);
        casesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        casesTable.setRowHeight(25);
        casesTable.getTableHeader().setReorderingAllowed(false);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        casesTable.setRowSorter(sorter);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(casesTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar at the bottom
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        contentPane.add(statusBar, BorderLayout.SOUTH);
        
        // Load cases data
        loadCasesData();
        
        pack();
        setSize(1000, 600);
        // Make the frame visible
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Refresh".equals(command)) {
            refreshCasesData();
        } else if ("Update Case".equals(command)) {
            updateSelectedCase();
        } else if ("Close Case".equals(command)) {
            closeSelectedCase();
        } else if ("Add Meeting".equals(command)) {
            showAddMeetingDialog();
        } else if ("Issue Verdict".equals(command)) {
            showVerdictDialog();
        }
    }
    
    /**
     * Creates the toolbar with action buttons
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setActionCommand("Refresh");
        refreshButton.addActionListener(this);
        toolBar.add(refreshButton);
        
        toolBar.addSeparator();
        
        // Update case button
        JButton updateButton = new JButton("Update Case");
        updateButton.setActionCommand("Update Case");
        updateButton.addActionListener(this);
        toolBar.add(updateButton);
        
        // Close case button
        JButton closeButton = new JButton("Close Case");
        closeButton.setActionCommand("Close Case");
        closeButton.addActionListener(this);
        toolBar.add(closeButton);
        
        toolBar.addSeparator();
        
        // Add meeting button
        JButton addMeetingButton = new JButton("Add Meeting");
        addMeetingButton.setActionCommand("Add Meeting");
        addMeetingButton.addActionListener(this);
        toolBar.add(addMeetingButton);
        
        // Issue verdict button
        JButton verdictButton = new JButton("Issue Verdict");
        verdictButton.setActionCommand("Issue Verdict");
        verdictButton.addActionListener(this);
        toolBar.add(verdictButton);
        
        toolBar.addSeparator();
        
        // Search field
        JTextField searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Search cases...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                filterCases(searchText);
            }
        });
        toolBar.add(searchField);
        
        return toolBar;
    }
    
    /**
     * Creates the table model with columns
     */
    private void createTableModel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        tableModel.addColumn("Case Code");
        tableModel.addColumn("Case Type");
        tableModel.addColumn("Accused");
        tableModel.addColumn("Opened Date");
        tableModel.addColumn("Status");
        tableModel.addColumn("Lawyer");
        tableModel.addColumn("Verdict");
    }
    
    /**
     * Loads all cases data into the table
     */
    private void loadCasesData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all cases that this judge is handling
        HashSet<Case> judgesCases = new HashSet<>();
        judgesCases.addAll(judge.getCasesHandled());
        judgesCases.addAll(judge.getCasesPresided());
        
        // Add each case to the table
        for (Case c : judgesCases) {
            Verdict verdict = c.getVerdict();
            tableModel.addRow(new Object[]{
                c.getCode(),
                c.getCaseType(),
                c.getAccused().getFirstName() + " " + c.getAccused().getLastName(),
                dateFormat.format(c.getOpenedDate()),
                c.getCaseStatus(),
                c.getLawyer() != null ? 
                    c.getLawyer().getFirstName() + " " + c.getLawyer().getLastName() : "N/A",
                verdict != null ? "Issued on " + dateFormat.format(verdict.getIssusedDate()) : "Not issued"
            });
        }
    }
    
    /**
     * Refreshes the cases data
     */
    private void refreshCasesData() {
        loadCasesData();
        JOptionPane.showMessageDialog(this, "Case data refreshed", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Filters cases based on search text
     */
    private void filterCases(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) casesTable.getRowSorter();
        
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }
    
    /**
     * Updates the selected case
     */
    private void updateSelectedCase() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected case code
        String caseCode = (String) casesTable.getValueAt(casesTable.convertRowIndexToModel(selectedRow), 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null) {
            JOptionPane.showMessageDialog(this, "Cannot find the selected case", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if case is already closed
        if (selectedCase.getCaseStatus() == Status.finished || selectedCase.getCaseStatus() == Status.canceled) {
            JOptionPane.showMessageDialog(this, "Cannot update a closed or canceled case", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show update case dialog
        showUpdateCaseDialog(selectedCase);
    }
    
    /**
     * Shows the dialog to update a case
     */
    private void showUpdateCaseDialog(Case caseToUpdate) {
        updateCaseDialog = new JDialog(JFrame.getFrames()[0], "Update Case", true);
        updateCaseDialog.getContentPane().setLayout(new BorderLayout());
        updateCaseDialog.setSize(400, 300);
        updateCaseDialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Status selection
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<Status> statusCombo = new JComboBox<>(Status.values());
        statusCombo.setSelectedItem(caseToUpdate.getCaseStatus());
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);
        
        // Lawyer assignment (if available)
        JLabel lawyerLabel = new JLabel("Assigned Lawyer:");
        JComboBox<Lawyer> lawyerCombo = new JComboBox<>();
        // Populate with lawyers from court system
        for (Lawyer lawyer : court.getAllLawyers().values()) {
            if (!(lawyer instanceof Judge)) {
                lawyerCombo.addItem(lawyer);
            }
        }
        if (caseToUpdate.getLawyer() != null) {
            lawyerCombo.setSelectedItem(caseToUpdate.getLawyer());
        }
        formPanel.add(lawyerLabel);
        formPanel.add(lawyerCombo);
        
        // Case type (specialization)
        JLabel typeLabel = new JLabel("Case Type:");
        JComboBox<Specialization> typeCombo = new JComboBox<>(Specialization.values());
        typeCombo.setSelectedItem(caseToUpdate.getCaseType());
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        
        updateCaseDialog.getContentPane().add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            // Update case details
            caseToUpdate.setCaseStatus((Status) statusCombo.getSelectedItem());
            caseToUpdate.setCaseType((Specialization) typeCombo.getSelectedItem());
            
            Lawyer selectedLawyer = (Lawyer) lawyerCombo.getSelectedItem();
            if (selectedLawyer != caseToUpdate.getLawyer()) {
                // If lawyer changed, remove case from old lawyer and add to new one
                if (caseToUpdate.getLawyer() != null) {
                    caseToUpdate.getLawyer().removeCase(caseToUpdate);
                }
                caseToUpdate.setLawyer(selectedLawyer);
                selectedLawyer.addCase(caseToUpdate);
            }
            
            JOptionPane.showMessageDialog(updateCaseDialog, "Case updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            updateCaseDialog.dispose();
            refreshCasesData();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> updateCaseDialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        updateCaseDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        updateCaseDialog.setVisible(true);
    }
    
    /**
     * Closes the selected case
     */
    private void closeSelectedCase() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to close", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected case code
        String caseCode = (String) casesTable.getValueAt(casesTable.convertRowIndexToModel(selectedRow), 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null) {
            JOptionPane.showMessageDialog(this, "Cannot find the selected case", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if case is already closed
        if (selectedCase.getCaseStatus() == Status.finished || selectedCase.getCaseStatus() == Status.canceled) {
            JOptionPane.showMessageDialog(this, "Case is already closed or canceled", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm with the user
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to close this case? This action cannot be undone.", 
                "Confirm Close Case", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Ask if user wants to add a verdict before closing
            choice = JOptionPane.showConfirmDialog(this, 
                    "Do you want to issue a verdict before closing this case?", 
                    "Issue Verdict", 
                    JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                showVerdictDialog(selectedCase);
            } else {
                // Close case without verdict
                if (judge.closeCase(selectedCase)) {
                    JOptionPane.showMessageDialog(this, "Case closed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCasesData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to close the case", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Shows dialog to add a meeting for the selected case
     */
    private void showAddMeetingDialog() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to add a meeting", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected case code
        String caseCode = (String) casesTable.getValueAt(casesTable.convertRowIndexToModel(selectedRow), 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null) {
            JOptionPane.showMessageDialog(this, "Cannot find the selected case", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if case is already closed
        if (selectedCase.getCaseStatus() == Status.finished || selectedCase.getCaseStatus() == Status.canceled) {
            JOptionPane.showMessageDialog(this, "Cannot add meetings to a closed or canceled case", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create and show the add meeting dialog
        addMeetingDialog = new JDialog(JFrame.getFrames()[0], "Add Meeting", true);
        addMeetingDialog.getContentPane().setLayout(new BorderLayout());
        addMeetingDialog.setSize(400, 300);
        addMeetingDialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Date field
        JLabel dateLabel = new JLabel("Meeting Date (dd/mm/yyyy):");
        JTextField dateField = new JTextField();
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        
        // Time field
        JLabel timeLabel = new JLabel("Meeting Time (HH:MM):");
        JTextField timeField = new JTextField();
        formPanel.add(timeLabel);
        formPanel.add(timeField);
        
        // Courtroom selection
        JLabel courtroomLabel = new JLabel("Courtroom:");
        JComboBox<Courtroom> courtroomCombo = new JComboBox<>();
        // Populate with available courtrooms
        for (Courtroom courtroom : court.getAllCourtrooms().values()) {
            courtroomCombo.addItem(courtroom);
        }
        formPanel.add(courtroomLabel);
        formPanel.add(courtroomCombo);
        
        // Case info (read-only)
        JLabel caseLabel = new JLabel("Case:");
        JTextField caseField = new JTextField(selectedCase.getCode() + " - " + selectedCase.getCaseType());
        caseField.setEditable(false);
        formPanel.add(caseLabel);
        formPanel.add(caseField);
        
        addMeetingDialog.getContentPane().add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Schedule Meeting");
        saveButton.addActionListener(e -> {
            try {
                // Parse date and time
                Date meetingDate = dateFormat.parse(dateField.getText().trim());
                String[] timeParts = timeField.getText().trim().split(":");
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1]);
                
                java.sql.Time meetingTime = new java.sql.Time(hours, minutes, 0);
                Courtroom selectedCourtroom = (Courtroom) courtroomCombo.getSelectedItem();
                
                // Check if meeting date is valid (after case opened date)
                if (meetingDate.before(selectedCase.getOpenedDate())) {
                    JOptionPane.showMessageDialog(addMeetingDialog, 
                            "Meeting date must be after the case opening date", 
                            "Invalid Date", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check for courtroom availability
                for (Meeting m : court.getAllMeetings().values()) {
                    if (m.getCourtroom().equals(selectedCourtroom) && 
                        m.getMeetingDate().equals(meetingDate) && 
                        m.getHour().equals(meetingTime)) {
                        JOptionPane.showMessageDialog(addMeetingDialog, 
                                "Courtroom is already booked at this time", 
                                "Scheduling Conflict", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Create and add the meeting
                Meeting newMeeting = new Meeting(meetingDate, meetingTime, selectedCourtroom, selectedCase);
                if (court.addMeeting(newMeeting) && selectedCase.addMeeting(newMeeting)) {
                    JOptionPane.showMessageDialog(addMeetingDialog, 
                            "Meeting scheduled successfully", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    addMeetingDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addMeetingDialog, 
                            "Failed to schedule meeting", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addMeetingDialog, 
                        "Invalid input format. Please check date and time formats.", 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> addMeetingDialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        addMeetingDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        addMeetingDialog.setVisible(true);
    }
    
    /**
     * Shows the verdict dialog for the selected case
     */
    private void showVerdictDialog() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to issue a verdict", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected case code
        String caseCode = (String) casesTable.getValueAt(casesTable.convertRowIndexToModel(selectedRow), 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null) {
            JOptionPane.showMessageDialog(this, "Cannot find the selected case", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show verdict dialog for this case
        showVerdictDialog(selectedCase);
    }
    
    /**
     * Shows the verdict dialog for a specific case
     */
    private void showVerdictDialog(Case selectedCase) {
        // Check if case already has a verdict
        if (selectedCase.getVerdict() != null) {
            JOptionPane.showMessageDialog(this, 
                    "This case already has a verdict. You cannot issue another verdict.", 
                    "Verdict Exists", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create and show verdict dialog
        // Use JFrame.getFrames()[0] as parent instead of SwingUtilities.getWindowAncestor()
        verdictDialog = new JDialog(JFrame.getFrames()[0], "Issue Verdict", true);
        verdictDialog.getContentPane().setLayout(new BorderLayout());
        verdictDialog.setSize(500, 400);
        verdictDialog.setLocationRelativeTo(null);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Case details
        StringBuilder caseDetails = new StringBuilder();
        caseDetails.append("Case: ").append(selectedCase.getCode()).append(" - ").append(selectedCase.getCaseType()).append("\n");
        caseDetails.append("Accused: ").append(selectedCase.getAccused().getFirstName()).append(" ").append(selectedCase.getAccused().getLastName()).append("\n");
        caseDetails.append("Opened Date: ").append(dateFormat.format(selectedCase.getOpenedDate())).append("\n");
        
        JTextArea caseDetailsArea = new JTextArea(caseDetails.toString());
        caseDetailsArea.setEditable(false);
        caseDetailsArea.setBackground(null);
        headerPanel.add(caseDetailsArea, BorderLayout.NORTH);
        
        verdictDialog.getContentPane().add(headerPanel, BorderLayout.NORTH);
        
        // Verdict form
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel verdictLabel = new JLabel("Verdict Summary:");
        JTextArea verdictArea = new JTextArea(10, 40);
        verdictArea.setLineWrap(true);
        verdictArea.setWrapStyleWord(true);
        JScrollPane verdictScrollPane = new JScrollPane(verdictArea);
        
        formPanel.add(verdictLabel, BorderLayout.NORTH);
        formPanel.add(verdictScrollPane, BorderLayout.CENTER);
        
        verdictDialog.getContentPane().add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton issueButton = new JButton("Issue Verdict");
        issueButton.addActionListener(e -> {
            String verdictSummary = verdictArea.getText().trim();
            if (verdictSummary.isEmpty()) {
                JOptionPane.showMessageDialog(verdictDialog, 
                        "Please enter verdict summary", 
                        "Missing Information", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create and save the verdict
            Verdict newVerdict = new Verdict(verdictSummary, new Date(), judge, selectedCase, null);
            
            if (court.addVerdict(newVerdict)) {
                // Close the case
                judge.closeCase(selectedCase);
                
                JOptionPane.showMessageDialog(verdictDialog, 
                        "Verdict issued and case closed successfully", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                verdictDialog.dispose();
                refreshCasesData();
            } else {
                JOptionPane.showMessageDialog(verdictDialog, 
                        "Failed to issue verdict", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> verdictDialog.dispose());
        
        buttonPanel.add(issueButton);
        buttonPanel.add(cancelButton);
        
        verdictDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        verdictDialog.setVisible(true);
    }
}