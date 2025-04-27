package view;

import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import control.Court;
import enums.Status;
import model.*;

/**
 * Panel for lawyers to manage their cases, add documents, meetings, and request appeals
 */
public class CaseLawyer extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private Court court;
    private Lawyer lawyer;
    
    // UI Components
    private JTable casesTable;
    private DefaultTableModel tableModel;
    private JButton addDocumentButton;
    private JButton addMeetingButton;
    private JButton requestAppealButton;
    private JButton refreshButton;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    // Colors for styling
    private final Color BUTTON_BG_COLOR = new Color(25, 42, 86); // Dark blue
    private final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    /**
     * Creates the case management panel for a lawyer
     * @param lawyer The lawyer who will use this panel
     */
    public CaseLawyer(Lawyer lawyer) {
        this.court = Main.court;
        this.lawyer = lawyer;
        
        setTitle("My Cases");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(50, 50, 950, 600);
        
        // Create main content panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);
        
        // Create table for cases
        JPanel tablePanel = createTablePanel();
        contentPane.add(tablePanel, BorderLayout.CENTER);
        
        // Create button panel for actions
        JPanel buttonPanel = createButtonPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize with all cases
        loadCases();
    }
    

    
    /**
     * Creates the table panel with cases list
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("My Cases"));
        
        // Create table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return String.class; // Case Code
                if (columnIndex == 1) return Status.class; // Status
                if (columnIndex == 2) return String.class; // Type
                if (columnIndex == 3) return String.class; // Accused
                if (columnIndex == 4) return String.class; // Open Date
                if (columnIndex == 5) return Boolean.class; // Has Verdict
                return Object.class;
            }
        };
        
        // Add columns
        tableModel.addColumn("Case Code");
        tableModel.addColumn("Status");
        tableModel.addColumn("Type");
        tableModel.addColumn("Accused");
        tableModel.addColumn("Open Date");
        tableModel.addColumn("Has Verdict");
        
        // Create table
        casesTable = new JTable(tableModel);
        casesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        casesTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer for Status column
        casesTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Status) {
                    Status status = (Status) value;
                    switch (status) {
                        case inProcess:
                            c.setForeground(new Color(0, 128, 0)); // Green
                            break;
                        case finished:
                            c.setForeground(new Color(0, 0, 128)); // Blue
                            break;
                        case canceled:
                            c.setForeground(new Color(128, 0, 0)); // Red
                            break;
                        default:
                            c.setForeground(Color.BLACK);
                    }
                }
                
                return c;
            }
        });
        
        // Custom renderer for Boolean (Has Verdict) column
        casesTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Boolean) {
                    boolean hasVerdict = (Boolean) value;
                    label.setText(hasVerdict ? "Yes" : "No");
                    label.setHorizontalAlignment(JLabel.CENTER);
                }
                
                return label;
            }
        });
        
        // Add selection listener
        casesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonState();
            }
        });
        
        // Add to scrollPane
        JScrollPane scrollPane = new JScrollPane(casesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the button panel with action buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        addDocumentButton = createStyledButton("Add Document");
        addDocumentButton.setEnabled(false);
        addDocumentButton.addActionListener(e -> addDocument());
        
        addMeetingButton = createStyledButton("Schedule Meeting");
        addMeetingButton.setEnabled(false);
        addMeetingButton.addActionListener(e -> addMeeting());
        
        requestAppealButton = createStyledButton("Request Appeal");
        requestAppealButton.setEnabled(false);
        requestAppealButton.addActionListener(e -> requestAppeal());
        
        refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadCases());
        
        panel.add(addDocumentButton);
        panel.add(addMeetingButton);
        panel.add(requestAppealButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Updates the button state based on selected case
     */
    private void updateButtonState() {
        int selectedRow = casesTable.getSelectedRow();
        
        if (selectedRow == -1) {
            addDocumentButton.setEnabled(false);
            addMeetingButton.setEnabled(false);
            requestAppealButton.setEnabled(false);
            return;
        }
        
        // Get selected case details
        String caseCode = (String) tableModel.getValueAt(selectedRow, 0);
        Status status = (Status) tableModel.getValueAt(selectedRow, 1);
        boolean hasVerdict = (Boolean) tableModel.getValueAt(selectedRow, 5);
        
        // Enable add document & meeting only for InProcess cases
        addDocumentButton.setEnabled(status == Status.inProcess);
        addMeetingButton.setEnabled(status == Status.inProcess);
        
        // Enable appeal request only for finished cases with verdict
        requestAppealButton.setEnabled(status == Status.finished && hasVerdict);
    }
    
    /**
     * Loads cases for this lawyer
     */
    private void loadCases() {
        tableModel.setRowCount(0);
        
        for (Case c : lawyer.getCasesHandled()) {
            tableModel.addRow(new Object[] {
                c.getCode(),
                c.getCaseStatus(),
                c.getCaseType(),
                c.getAccused().getFirstName() + " " + c.getAccused().getLastName(),
                dateFormat.format(c.getOpenedDate()),
                c.getVerdict() != null
            });
        }
        
        updateButtonState();
    }
    
    /**
     * Add a document to the selected case
     */
    private void addDocument() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert to model index in case of sorting
        int modelRow = casesTable.convertRowIndexToModel(selectedRow);
        String caseCode = (String) tableModel.getValueAt(modelRow, 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null || selectedCase.getCaseStatus() != Status.inProcess) {
            JOptionPane.showMessageDialog(this, 
                    "Cannot add document to this case.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create document dialog
        JPanel dialogPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // Title field
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(new JLabel("Document Title:"), BorderLayout.WEST);
        JTextField titleField = new JTextField(20);
        titlePanel.add(titleField, BorderLayout.CENTER);
        dialogPanel.add(titlePanel);
        
        // Content field
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JLabel("Document Content:"), BorderLayout.NORTH);
        JTextArea contentArea = new JTextArea(10, 30);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(contentPanel);
        
        // Date field
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("Document Date (dd/MM/yyyy):"), BorderLayout.WEST);
        JFormattedTextField dateField = new JFormattedTextField(dateFormat);
        dateField.setValue(new Date()); // Set to current date
        datePanel.add(dateField, BorderLayout.CENTER);
        dialogPanel.add(datePanel);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, 
                "Add Document to Case " + caseCode, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String content = contentArea.getText().trim();
                Date documentDate = dateFormat.parse(dateField.getText());
                
                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                            "Title and content cannot be empty.", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create document
                Document document = new Document(title, content, documentDate, selectedCase);
                
                // Add to case and court
                selectedCase.addDocument(document);
                if (court.addDocument(document)) {
                    JOptionPane.showMessageDialog(this, 
                            "Document added successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Save changes
                    Main.save();
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to add document.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, 
                        "Invalid date format. Please use dd/MM/yyyy.", 
                        "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Add a meeting to the selected case
     */
    private void addMeeting() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert to model index in case of sorting
        int modelRow = casesTable.convertRowIndexToModel(selectedRow);
        String caseCode = (String) tableModel.getValueAt(modelRow, 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null || selectedCase.getCaseStatus() != Status.inProcess) {
            JOptionPane.showMessageDialog(this, 
                    "Cannot schedule meeting for this case.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create meeting dialog
        JPanel dialogPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // Date field
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("Meeting Date (dd/MM/yyyy):"), BorderLayout.WEST);
        JFormattedTextField dateField = new JFormattedTextField(dateFormat);
        
        // Set default date to tomorrow
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        dateField.setValue(cal.getTime());
        
        datePanel.add(dateField, BorderLayout.CENTER);
        dialogPanel.add(datePanel);
        
        // Time field
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(new JLabel("Meeting Time:"), BorderLayout.WEST);
        String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        JComboBox<String> timeComboBox = new JComboBox<>(times);
        timePanel.add(timeComboBox, BorderLayout.CENTER);
        dialogPanel.add(timePanel);
        
        // Courtroom field
        JPanel courtroomPanel = new JPanel(new BorderLayout());
        courtroomPanel.add(new JLabel("Courtroom:"), BorderLayout.WEST);
        
        // Get available courtrooms from the department
        Department dept = selectedCase.getLawyer().getDepartment();
        Collection<Courtroom> courtrooms = dept != null ? 
                dept.getCourtrooms() : court.getAllCourtrooms().values();
        
        JComboBox<Courtroom> courtroomComboBox = new JComboBox<>();
        for (Courtroom courtroom : courtrooms) {
            courtroomComboBox.addItem(courtroom);
        }
        
        // Custom renderer for courtroom display
        courtroomComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Courtroom) {
                    Courtroom courtroom = (Courtroom) value;
                    Department dept = courtroom.getDepartment();
                    value = "Room " + courtroom.getCourtroomNumber() + 
                            (dept != null ? " (" + dept.getName() + ")" : "");
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        courtroomPanel.add(courtroomComboBox, BorderLayout.CENTER);
        dialogPanel.add(courtroomPanel);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, 
                "Schedule Meeting for Case " + caseCode, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Date meetingDate = dateFormat.parse(dateField.getText());
                String timeString = (String) timeComboBox.getSelectedItem();
                Time meetingTime = Time.valueOf(timeString + ":00");
                Courtroom courtroom = (Courtroom) courtroomComboBox.getSelectedItem();
                
                if (courtroom == null) {
                    JOptionPane.showMessageDialog(this, 
                            "Please select a courtroom.", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if date is in the future
                if (meetingDate.before(new Date())) {
                    JOptionPane.showMessageDialog(this, 
                            "Meeting date must be in the future.", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if courtroom is available at that time
                for (Meeting m : court.getAllMeetings().values()) {
                    if (m.getCourtroom().equals(courtroom) && 
                            m.getMeetingDate().equals(meetingDate) &&
                            m.getHour().equals(meetingTime)) {
                        JOptionPane.showMessageDialog(this, 
                                "This courtroom is already booked at the selected time.", 
                                "Unavailable", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Create meeting
                Meeting meeting = new Meeting(meetingDate, meetingTime, courtroom, selectedCase);
                
                // Add to case and court
                if (court.addMeeting(meeting)) {
                    JOptionPane.showMessageDialog(this, 
                            "Meeting scheduled successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Save changes
                    Main.save();
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to schedule meeting.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, 
                        "Invalid date format. Please use dd/MM/yyyy.", 
                        "Format Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Request an appeal for the selected case
     */
    private void requestAppeal() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert to model index in case of sorting
        int modelRow = casesTable.convertRowIndexToModel(selectedRow);
        String caseCode = (String) tableModel.getValueAt(modelRow, 0);
        Case selectedCase = court.getRealCase(caseCode);
        
        if (selectedCase == null || selectedCase.getCaseStatus() != Status.finished || selectedCase.getVerdict() == null) {
            JOptionPane.showMessageDialog(this, 
                    "Cannot request appeal for this case.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if an appeal already exists
        if (selectedCase.getVerdict().getAppeal() != null) {
            JOptionPane.showMessageDialog(this, 
                    "An appeal has already been filed for this case.", 
                    "Appeal Exists", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create appeal dialog
        JPanel dialogPanel = new JPanel(new BorderLayout());
        
        // Appeal summary
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.add(new JLabel("Appeal Reason:"), BorderLayout.NORTH);
        JTextArea summaryArea = new JTextArea(10, 30);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(summaryPanel, BorderLayout.CENTER);
        
        // Current verdict information
        JPanel verdictInfoPanel = new JPanel(new BorderLayout());
        verdictInfoPanel.setBorder(BorderFactory.createTitledBorder("Current Verdict Information"));
        
        Verdict currentVerdict = selectedCase.getVerdict();
        JTextArea verdictInfoArea = new JTextArea(5, 30);
        verdictInfoArea.setText("Verdict ID: " + currentVerdict.getVerdictID() + "\n" +
                "Issued By: " + currentVerdict.getJudge().getFirstName() + " " + currentVerdict.getJudge().getLastName() + "\n" +
                "Date: " + dateFormat.format(currentVerdict.getIssusedDate()) + "\n\n" +
                "Summary: " + currentVerdict.getVerdictSummary());
        verdictInfoArea.setEditable(false);
        verdictInfoArea.setWrapStyleWord(true);
        verdictInfoArea.setLineWrap(true);
        verdictInfoPanel.add(new JScrollPane(verdictInfoArea), BorderLayout.CENTER);
        
        dialogPanel.add(verdictInfoPanel, BorderLayout.NORTH);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, 
                "Request Appeal for Case " + caseCode, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String appealSummary = summaryArea.getText().trim();
                
                if (appealSummary.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                            "Appeal reason cannot be empty.", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create new verdict (placeholder) that will replace the current one
                // In a real system, this would be done by a judge later
                Judge judge = null;
                for (Lawyer l : court.getAllLawyers().values()) {
                    if (l instanceof Judge) {
                        judge = (Judge) l;
                        break;
                    }
                }
                
                if (judge == null) {
                    JOptionPane.showMessageDialog(this, 
                            "No judge available to process appeal.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Verdict newVerdict = new Verdict(
                        "Pending review of appeal", 
                        new Date(), 
                        judge, 
                        selectedCase,
                        null
                );
                
                // Create appeal
                Appeal appeal = new Appeal(
                        appealSummary,
                        new Date(),
                        currentVerdict,
                        newVerdict
                );
                
                // Link appeal to current verdict
                currentVerdict.setAppeal(appeal);
                
                // Add to court
                if (court.addAppeal(appeal)) {
                    JOptionPane.showMessageDialog(this, 
                            "Appeal request submitted successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Save changes
                    Main.save();
                    
                    // Refresh to update button states
                    loadCases();
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to submit appeal request.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(20, 37, 81), 1));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(35, 52, 96)); // Lighter shade when hovering
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_BG_COLOR);
                }
            }
        });
        
        return button;
    }
}
