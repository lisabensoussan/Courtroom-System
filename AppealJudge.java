package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import control.Court;
import enums.Status;
import model.*;

/**
 * This class provides an interface for judges to review and process pending appeals
 * submitted by lawyers for cases with verdicts.
 */
public class AppealJudge extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private JPanel contentPane;
    private JTable appealsTable;
    private DefaultTableModel tableModel;
    private Judge judge;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // For appeal details
    private JTextArea originalVerdictArea;
    private JTextArea appealReasonArea;
    private JTextArea newVerdictArea;
    private JPanel detailsPanel;
    private JTextField caseField;
    private JTextField accusedField;
    private JTextField appealDateField;
    private JTextField originalJudgeField;
    
    // Buttons
    private JButton acceptAppealButton;
    private JButton rejectAppealButton;
    private JButton refreshButton;
    
    /**
     * Create the internal frame.
     */
    public AppealJudge(Judge judge) {
        super("Appeals Management", true, true, true, true);
        this.judge = judge;
        this.court = Main.court;
        
        setBounds(50, 50, 1000, 700);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create toolbar with buttons
        JToolBar toolBar = createToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);
        
        // Create split pane for table and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.4);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Create table model and table
        createTableModel();
        appealsTable = new JTable(tableModel);
        appealsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appealsTable.setRowHeight(25);
        appealsTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener
        appealsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedAppeal();
                    updateButtonState();
                }
            }
        });
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        appealsTable.setRowSorter(sorter);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(appealsTable);
        splitPane.setTopComponent(scrollPane);
        
        // Create details panel
        detailsPanel = createDetailsPanel();
        splitPane.setBottomComponent(detailsPanel);
        
        // Status bar at the bottom
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pending appeals: " + tableModel.getRowCount());
        statusBar.add(statusLabel);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        
        // Load appeal data
        loadAppealData();
        
        // Add window controls for macOS
        addWindowControls();
        
        setSize(1000, 700);
        setVisible(true);
    }
    
    /**
     * Adds window control button functionality
     */
    private void addWindowControls() {
        // Add key binding for close (Command+W)
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        
        // Create close action
        Action closeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                doDefaultCloseAction();
            }
        };
        
        // Add key binding for Command+W (standard macOS close shortcut)
        KeyStroke closeKey = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        inputMap.put(closeKey, "close");
        actionMap.put("close", closeAction);
        
        // Add a close listener to ensure proper disposal
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                // Nothing special needed here
            }
        });
        
        // Make sure the frame is properly set up for macOS window controls
        putClientProperty("JInternalFrame.isPalette", Boolean.FALSE);
        putClientProperty("JInternalFrame.systemMenuVisible", Boolean.TRUE);
    }
    
    /**
     * Creates the toolbar with action buttons
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.setActionCommand("Refresh");
        refreshButton.addActionListener(this);
        toolBar.add(refreshButton);
        
        toolBar.addSeparator();
        
        // Accept Appeal button
        acceptAppealButton = new JButton("Accept Appeal & Issue New Verdict");
        acceptAppealButton.setActionCommand("Accept");
        acceptAppealButton.addActionListener(this);
        acceptAppealButton.setEnabled(false);
        toolBar.add(acceptAppealButton);
        
        // Reject Appeal button
        rejectAppealButton = new JButton("Reject Appeal");
        rejectAppealButton.setActionCommand("Reject");
        rejectAppealButton.addActionListener(this);
        rejectAppealButton.setEnabled(false);
        toolBar.add(rejectAppealButton);
        
        return toolBar;
    }
    
    /**
     * Creates the table model with columns
     */
    private void createTableModel() {
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Appeal ID
                return String.class;
            }
        };
        
        tableModel.addColumn("Appeal ID");
        tableModel.addColumn("Case Code");
        tableModel.addColumn("Original Verdict ID");
        tableModel.addColumn("Appeal Date");
        tableModel.addColumn("Status");
        tableModel.addColumn("Requesting Lawyer");
    }
    
    /**
     * Creates the details panel for displaying appeal information
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Appeal Details"));
        
        // Create top info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Case field
        JLabel caseLabel = new JLabel("Case:");
        caseField = new JTextField();
        caseField.setEditable(false);
        infoPanel.add(caseLabel);
        infoPanel.add(caseField);
        
        // Accused field
        JLabel accusedLabel = new JLabel("Accused:");
        accusedField = new JTextField();
        accusedField.setEditable(false);
        infoPanel.add(accusedLabel);
        infoPanel.add(accusedField);
        
        // Appeal date field
        JLabel dateLabel = new JLabel("Appeal Date:");
        appealDateField = new JTextField();
        appealDateField.setEditable(false);
        infoPanel.add(dateLabel);
        infoPanel.add(appealDateField);
        
        // Original judge field
        JLabel judgeLabel = new JLabel("Original Judge:");
        originalJudgeField = new JTextField();
        originalJudgeField.setEditable(false);
        infoPanel.add(judgeLabel);
        infoPanel.add(originalJudgeField);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Create content panel with 3 text areas
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Original verdict area
        JPanel originalVerdictPanel = new JPanel(new BorderLayout());
        originalVerdictPanel.setBorder(BorderFactory.createTitledBorder("Original Verdict"));
        originalVerdictArea = new JTextArea(5, 30);
        originalVerdictArea.setEditable(false);
        originalVerdictArea.setLineWrap(true);
        originalVerdictArea.setWrapStyleWord(true);
        JScrollPane originalScrollPane = new JScrollPane(originalVerdictArea);
        originalVerdictPanel.add(originalScrollPane, BorderLayout.CENTER);
        contentPanel.add(originalVerdictPanel);
        
        // Appeal reason area
        JPanel appealReasonPanel = new JPanel(new BorderLayout());
        appealReasonPanel.setBorder(BorderFactory.createTitledBorder("Appeal Reason"));
        appealReasonArea = new JTextArea(5, 30);
        appealReasonArea.setEditable(false);
        appealReasonArea.setLineWrap(true);
        appealReasonArea.setWrapStyleWord(true);
        JScrollPane appealScrollPane = new JScrollPane(appealReasonArea);
        appealReasonPanel.add(appealScrollPane, BorderLayout.CENTER);
        contentPanel.add(appealReasonPanel);
        
        // New verdict area
        JPanel newVerdictPanel = new JPanel(new BorderLayout());
        newVerdictPanel.setBorder(BorderFactory.createTitledBorder("New Verdict (Enter Your Decision)"));
        newVerdictArea = new JTextArea(5, 30);
        newVerdictArea.setLineWrap(true);
        newVerdictArea.setWrapStyleWord(true);
        // Add document listener to enable/disable accept button
        newVerdictArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }
        });
        JScrollPane newScrollPane = new JScrollPane(newVerdictArea);
        newVerdictPanel.add(newScrollPane, BorderLayout.CENTER);
        contentPanel.add(newVerdictPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Loads all pending appeal data into the table
     */
    private void loadAppealData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all appeals in the system
            for (Appeal appeal : court.getAllAppeals().values()) {
                Verdict originalVerdict = appeal.getCurrentVerdict();
                Verdict newVerdict = appeal.getNewVerdict();
                
                // Only show appeals with "empty" new verdicts (pending approval)
                if (originalVerdict != null && newVerdict != null && 
                        newVerdict.getVerdictSummary().equals("Pending review of appeal")) {
                    
                    Case c = originalVerdict.getCasee();
                    String status = "Pending";
                    String requestingLawyer = c.getLawyer() != null ? 
                            c.getLawyer().getFirstName() + " " + c.getLawyer().getLastName() : "Unknown";
                    
                    tableModel.addRow(new Object[] {
                        appeal.getAppealID(),
                        c.getCode(),
                        originalVerdict.getVerdictID(),
                        dateFormat.format(appeal.getAppealDate()),
                        status,
                        requestingLawyer
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading appeals: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Update button states
        updateButtonState();
    }
    
    /**
     * Updates the button states based on selection
     */
    private void updateButtonState() {
        boolean hasSelection = appealsTable.getSelectedRow() != -1;
        boolean hasNewVerdictText = !newVerdictArea.getText().trim().isEmpty();
        
        acceptAppealButton.setEnabled(hasSelection && hasNewVerdictText);
        rejectAppealButton.setEnabled(hasSelection);
    }
    
    /**
     * Displays the details of the selected appeal
     */
    private void displaySelectedAppeal() {
        int selectedRow = appealsTable.getSelectedRow();
        if (selectedRow == -1) {
            // Clear details if no selection
            clearDetails();
            return;
        }
        
        try {
            // Get the selected appeal ID
            int appealId = Integer.parseInt(tableModel.getValueAt(appealsTable.convertRowIndexToModel(selectedRow), 0).toString());
            Appeal selectedAppeal = court.getRealAppeal(appealId);
            
            if (selectedAppeal == null) {
                JOptionPane.showMessageDialog(this, "Cannot find the selected appeal", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get related objects
            Verdict originalVerdict = selectedAppeal.getCurrentVerdict();
            Case c = originalVerdict.getCasee();
            
            // Update info fields
            caseField.setText(c.getCode() + " - " + c.getCaseType());
            accusedField.setText(c.getAccused().getFirstName() + " " + c.getAccused().getLastName());
            appealDateField.setText(dateFormat.format(selectedAppeal.getAppealDate()));
            originalJudgeField.setText(originalVerdict.getJudge().getFirstName() + " " + originalVerdict.getJudge().getLastName());
            
            // Update text areas
            originalVerdictArea.setText(originalVerdict.getVerdictSummary());
            appealReasonArea.setText(selectedAppeal.getAppealSummary());
            newVerdictArea.setText("");  // Clear for new input
            
            // Enable/disable buttons
            updateButtonState();
        } catch (Exception e) {
            System.err.println("Error displaying appeal: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying appeal details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Clears the details panel
     */
    private void clearDetails() {
        caseField.setText("");
        accusedField.setText("");
        appealDateField.setText("");
        originalJudgeField.setText("");
        originalVerdictArea.setText("");
        appealReasonArea.setText("");
        newVerdictArea.setText("");
        
        // Disable action buttons
        acceptAppealButton.setEnabled(false);
        rejectAppealButton.setEnabled(false);
    }
    
    /**
     * Accepts an appeal and issues a new verdict
     */
    private void acceptAppeal() {
        int selectedRow = appealsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        try {
            // Get the selected appeal ID
            int appealId = Integer.parseInt(tableModel.getValueAt(appealsTable.convertRowIndexToModel(selectedRow), 0).toString());
            Appeal selectedAppeal = court.getRealAppeal(appealId);
            
            if (selectedAppeal == null) {
                JOptionPane.showMessageDialog(this, "Cannot find the selected appeal", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String newVerdictText = newVerdictArea.getText().trim();
            if (newVerdictText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a new verdict decision", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirm with the user
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to accept this appeal and issue a new verdict?\nThis action cannot be undone.",
                    "Confirm Appeal Acceptance",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // Get related objects
                Verdict originalVerdict = selectedAppeal.getCurrentVerdict();
                Verdict pendingVerdict = selectedAppeal.getNewVerdict();
                Case c = originalVerdict.getCasee();
                
                // Update the pending verdict with the new text and this judge
                pendingVerdict.setVerdictSummary(newVerdictText);
                pendingVerdict.setJudge(judge);
                pendingVerdict.setIssusedDate(new Date());
                
                // Update case status
                c.setCaseStatus(Status.finished);
                
          
                
                JOptionPane.showMessageDialog(this,
                        "Appeal accepted and new verdict issued successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh data
                loadAppealData();
            }
        } catch (Exception e) {
            System.err.println("Error accepting appeal: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error processing appeal: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Rejects an appeal and keeps the original verdict
     */
    private void rejectAppeal() {
        int selectedRow = appealsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        try {
            // Get the selected appeal ID
            int appealId = Integer.parseInt(tableModel.getValueAt(appealsTable.convertRowIndexToModel(selectedRow), 0).toString());
            Appeal selectedAppeal = court.getRealAppeal(appealId);
            
            if (selectedAppeal == null) {
                JOptionPane.showMessageDialog(this, "Cannot find the selected appeal", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ask for rejection reason
            String rejectionReason = JOptionPane.showInputDialog(this,
                    "Please enter a reason for rejecting this appeal:",
                    "Reject Appeal",
                    JOptionPane.QUESTION_MESSAGE);
            
            if (rejectionReason == null) {
                // User canceled
                return;
            }
            
            if (rejectionReason.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide a reason for rejection", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirm with the user
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reject this appeal?\nThis action cannot be undone.",
                    "Confirm Appeal Rejection",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // Get related objects
                Verdict originalVerdict = selectedAppeal.getCurrentVerdict();
                Verdict pendingVerdict = selectedAppeal.getNewVerdict();
                Case c = originalVerdict.getCasee();
                
                // Update the pending verdict with rejection reason but keep it as rejected
                pendingVerdict.setVerdictSummary("Appeal REJECTED: " + rejectionReason + 
                        "\n\nOriginal verdict stands: " + originalVerdict.getVerdictSummary());
                pendingVerdict.setJudge(judge);
                pendingVerdict.setIssusedDate(new Date());
                
                // Case remains with status finished
                c.setCaseStatus(Status.finished);
                
           
                
                JOptionPane.showMessageDialog(this,
                        "Appeal rejected successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh data
                loadAppealData();
            }
        } catch (Exception e) {
            System.err.println("Error rejecting appeal: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error rejecting appeal: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Refreshes the appeal data
     */
    private void refreshAppealData() {
        loadAppealData();
        clearDetails();
        JOptionPane.showMessageDialog(this, "Appeal data refreshed", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
            case "Refresh":
                refreshAppealData();
                break;
                
            case "Accept":
                acceptAppeal();
                break;
                
            case "Reject":
                rejectAppeal();
                break;
        }
    }
}