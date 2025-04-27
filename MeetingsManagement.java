package view;

import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import control.Court;
import enums.Status;
import model.*;

/**
 * Panel for lawyers to view and manage upcoming meetings
 */
public class MeetingsManagement extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private Court court;
    private Lawyer lawyer;
    
    // UI Components
    private JTable meetingsTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JLabel meetingDateLabel;
    private JLabel meetingTimeLabel;
    private JLabel courtroomLabel;
    private JLabel departmentLabel;
    private JLabel caseCodeLabel;
    private JLabel caseStatusLabel;
    private JLabel accusedLabel;
    private JButton rescheduleButton;
    private JButton cancelButton;
    private JButton refreshButton;
    private JComboBox<String> viewFilterComboBox;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    // Colors for styling
    private final Color BUTTON_BG_COLOR = new Color(25, 42, 86); // Dark blue
    private final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    /**
     * Creates the meetings management panel for a lawyer
     * @param lawyer The lawyer who will use this panel
     */
    public MeetingsManagement(Lawyer lawyer) {
        this.court = Main.court;
        this.lawyer = lawyer;
        
        setTitle("My Meetings");
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
        
        // Create top panel with filter controls
        JPanel filterPanel = createFilterPanel();
        contentPane.add(filterPanel, BorderLayout.NORTH);
        
        // Create main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Create table panel for meetings
        JPanel tablePanel = createTablePanel();
        splitPane.setTopComponent(tablePanel);
        
        // Create details panel for selected meeting
        detailsPanel = createDetailsPanel();
        splitPane.setBottomComponent(detailsPanel);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize with all upcoming meetings
        loadMeetings();
    }
    
    /**
     * Creates the filter panel with view options
     */
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Filter Meetings"));
        
        // View filter
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewPanel.add(new JLabel("View:"));
        
        viewFilterComboBox = new JComboBox<>(new String[] {
            "All Upcoming Meetings",
            "Today's Meetings",
            "This Week's Meetings",
            "This Month's Meetings"
        });
        
        viewFilterComboBox.addActionListener(e -> loadMeetings());
        viewPanel.add(viewFilterComboBox);
        
        panel.add(viewPanel, BorderLayout.WEST);
        
        // Calendar icon
        JLabel calendarIcon = new JLabel(UIManager.getIcon("FileView.directoryIcon"));
        calendarIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.add(calendarIcon, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the table panel with meetings list
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Upcoming Meetings"));
        
        // Create table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Meeting ID
                if (columnIndex == 1) return String.class; // Date
                if (columnIndex == 2) return String.class; // Time
                if (columnIndex == 3) return String.class; // Courtroom
                if (columnIndex == 4) return String.class; // Case
                if (columnIndex == 5) return String.class; // Accused
                return Object.class;
            }
        };
        
        // Add columns
        tableModel.addColumn("Meeting ID");
        tableModel.addColumn("Date");
        tableModel.addColumn("Time");
        tableModel.addColumn("Courtroom");
        tableModel.addColumn("Case");
        tableModel.addColumn("Accused");
        
        // Create table
        meetingsTable = new JTable(tableModel);
        meetingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        meetingsTable.getTableHeader().setReorderingAllowed(false);
        
        // Enable sorting
        meetingsTable.setAutoCreateRowSorter(true);
        
        // Row color renderer (highlight today's meetings)
        meetingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String dateStr = (String) tableModel.getValueAt(modelRow, 1);
                    try {
                        Date meetingDate = dateFormat.parse(dateStr);
                        Calendar cal1 = Calendar.getInstance();
                        Calendar cal2 = Calendar.getInstance();
                        cal1.setTime(meetingDate);
                        
                        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && 
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                            c.setBackground(new Color(230, 255, 230)); // Light green for today
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } catch (ParseException e) {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        // Add selection listener to update details
        meetingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMeetingDetails();
            }
        });
        
        // Add to scrollPane
        JScrollPane scrollPane = new JScrollPane(meetingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the details panel for selected meeting
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Meeting Details"));
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Meeting details section
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Meeting Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        meetingDateLabel = new JLabel("-");
        panel.add(meetingDateLabel, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("Meeting Time:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 0;
        meetingTimeLabel = new JLabel("-");
        panel.add(meetingTimeLabel, gbc);
        
        // Courtroom details
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Courtroom:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        courtroomLabel = new JLabel("-");
        panel.add(courtroomLabel, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(new JLabel("Department:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        departmentLabel = new JLabel("-");
        panel.add(departmentLabel, gbc);
        
        // Case details
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Case Code:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        caseCodeLabel = new JLabel("-");
        panel.add(caseCodeLabel, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(new JLabel("Case Status:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 2;
        caseStatusLabel = new JLabel("-");
        panel.add(caseStatusLabel, gbc);
        
        // Accused details
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Accused:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        accusedLabel = new JLabel("-");
        panel.add(accusedLabel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the button panel with action buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        rescheduleButton = createStyledButton("Reschedule Meeting");
        rescheduleButton.setEnabled(false);
        rescheduleButton.addActionListener(e -> rescheduleMeeting());
        
        cancelButton = createStyledButton("Cancel Meeting");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelMeeting());
        
        refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadMeetings());
        
        panel.add(rescheduleButton);
        panel.add(cancelButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Loads meetings based on current filter
     */
    private void loadMeetings() {
        tableModel.setRowCount(0);
        
        // Get the selected filter
        String filter = (String) viewFilterComboBox.getSelectedItem();
        
        // Calculate date ranges
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date endOfWeek = cal.getTime();
        
        cal.setTime(today);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endOfMonth = cal.getTime();
        
        cal.setTime(today); // Reset cal to today
        
        // Create a list of upcoming meetings for this lawyer
        List<Meeting> upcomingMeetings = new ArrayList<>();
        
        // Get the lawyer's cases
        for (Case caseObj : lawyer.getCasesHandled()) {
            // Get meetings for each case
            for (Meeting meeting : caseObj.getMeetingsList()) {
                Date meetingDate = meeting.getMeetingDate();
                
                // Apply filter
                if (filter.equals("All Upcoming Meetings")) {
                    if (meetingDate.after(today) || isSameDay(meetingDate, today)) {
                        upcomingMeetings.add(meeting);
                    }
                } else if (filter.equals("Today's Meetings")) {
                    if (isSameDay(meetingDate, today)) {
                        upcomingMeetings.add(meeting);
                    }
                } else if (filter.equals("This Week's Meetings")) {
                    if ((meetingDate.after(today) || isSameDay(meetingDate, today)) && 
                        meetingDate.before(endOfWeek)) {
                        upcomingMeetings.add(meeting);
                    }
                } else if (filter.equals("This Month's Meetings")) {
                    if ((meetingDate.after(today) || isSameDay(meetingDate, today)) && 
                        meetingDate.before(endOfMonth)) {
                        upcomingMeetings.add(meeting);
                    }
                }
            }
        }
        
        // Sort by date
        Collections.sort(upcomingMeetings, (m1, m2) -> m1.getMeetingDate().compareTo(m2.getMeetingDate()));
        
        // Add to table
        for (Meeting meeting : upcomingMeetings) {
            Case caseObj = meeting.getCasee();
            Accused accused = caseObj.getAccused();
            Courtroom courtroom = meeting.getCourtroom();
            
            tableModel.addRow(new Object[] {
                meeting.getMeetingID(),
                dateFormat.format(meeting.getMeetingDate()),
                meeting.getHour() != null ? timeFormat.format(meeting.getHour()) : "-",
                "Room " + courtroom.getCourtroomNumber(),
                caseObj.getCode(),
                accused.getFirstName() + " " + accused.getLastName()
            });
        }
        
        // Clear details
        clearDetails();
    }
    
    /**
     * Updates meeting details based on selected meeting
     */
    private void updateMeetingDetails() {
        int selectedRow = meetingsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            clearDetails();
            return;
        }
        
        // Convert row index to model index (in case of sorting)
        int modelRow = meetingsTable.convertRowIndexToModel(selectedRow);
        
        // Get meeting ID
        int meetingId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Find meeting
        Meeting meeting = null;
        for (Case caseObj : lawyer.getCasesHandled()) {
            for (Meeting m : caseObj.getMeetingsList()) {
                if (m.getMeetingID() == meetingId) {
                    meeting = m;
                    break;
                }
            }
            if (meeting != null) {
                break;
            }
        }
        
        if (meeting == null) {
            clearDetails();
            return;
        }
        
        // Update details
        Case caseObj = meeting.getCasee();
        Accused accused = caseObj.getAccused();
        Courtroom courtroom = meeting.getCourtroom();
        Department department = courtroom.getDepartment();
        
        meetingDateLabel.setText(dateFormat.format(meeting.getMeetingDate()));
        meetingTimeLabel.setText(meeting.getHour() != null ? timeFormat.format(meeting.getHour()) : "-");
        courtroomLabel.setText("Room " + courtroom.getCourtroomNumber());
        departmentLabel.setText(department != null ? department.getName() : "Not Assigned");
        caseCodeLabel.setText(caseObj.getCode());
        caseStatusLabel.setText(caseObj.getCaseStatus().toString());
        accusedLabel.setText(accused.getFirstName() + " " + accused.getLastName());
        
        // Only enable reschedule/cancel for future meetings and active cases
        boolean canModify = caseObj.getCaseStatus() == Status.inProcess && 
                          (meeting.getMeetingDate().after(new Date()) || 
                           isSameDay(meeting.getMeetingDate(), new Date()));
        
        rescheduleButton.setEnabled(canModify);
        cancelButton.setEnabled(canModify);
    }
    
    /**
     * Clears the meeting details panel
     */
    private void clearDetails() {
        meetingDateLabel.setText("-");
        meetingTimeLabel.setText("-");
        courtroomLabel.setText("-");
        departmentLabel.setText("-");
        caseCodeLabel.setText("-");
        caseStatusLabel.setText("-");
        accusedLabel.setText("-");
        
        rescheduleButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
    
    /**
     * Reschedules the selected meeting
     */
    private void rescheduleMeeting() {
        int selectedRow = meetingsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert to model index in case of sorting
        int modelRow = meetingsTable.convertRowIndexToModel(selectedRow);
        int meetingId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Find meeting
        Meeting meeting = null;
        for (Case caseObj : lawyer.getCasesHandled()) {
            for (Meeting m : caseObj.getMeetingsList()) {
                if (m.getMeetingID() == meetingId) {
                    meeting = m;
                    break;
                }
            }
            if (meeting != null) {
                break;
            }
        }
        
        if (meeting == null) return;
        
        // Create reschedule dialog
        JPanel dialogPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // Date field
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("New Date (dd/MM/yyyy):"), BorderLayout.WEST);
        JFormattedTextField dateField = new JFormattedTextField(dateFormat);
        
        // Set default date to tomorrow
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        dateField.setValue(cal.getTime());
        
        datePanel.add(dateField, BorderLayout.CENTER);
        dialogPanel.add(datePanel);
        
        // Time field
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(new JLabel("New Time:"), BorderLayout.WEST);
        String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        JComboBox<String> timeComboBox = new JComboBox<>(times);
        
        // Set default to current time if possible
        if (meeting.getHour() != null) {
            String currentTime = timeFormat.format(meeting.getHour());
            for (int i = 0; i < times.length; i++) {
                if (times[i].equals(currentTime)) {
                    timeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        timePanel.add(timeComboBox, BorderLayout.CENTER);
        dialogPanel.add(timePanel);
        
        // Courtroom field
        JPanel courtroomPanel = new JPanel(new BorderLayout());
        courtroomPanel.add(new JLabel("New Courtroom:"), BorderLayout.WEST);
        
        // Get available courtrooms
        JComboBox<Courtroom> courtroomComboBox = new JComboBox<>();
        
        // First add the current courtroom
        courtroomComboBox.addItem(meeting.getCourtroom());
        
        // Then add other courtrooms
        for (Courtroom courtroom : court.getAllCourtrooms().values()) {
            if (!courtroom.equals(meeting.getCourtroom())) {
                courtroomComboBox.addItem(courtroom);
            }
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
                "Reschedule Meeting #" + meetingId, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Date newDate = dateFormat.parse(dateField.getText());
                String timeString = (String) timeComboBox.getSelectedItem();
                Time newTime = Time.valueOf(timeString + ":00");
                Courtroom newCourtroom = (Courtroom) courtroomComboBox.getSelectedItem();
                
                // Check if date is in the future
                if (newDate.before(new Date())) {
                    JOptionPane.showMessageDialog(this, 
                            "Meeting date must be in the future.", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if courtroom is available at that time
                for (Meeting m : court.getAllMeetings().values()) {
                    if (m.getMeetingID() != meetingId && // Skip comparing with self
                        m.getCourtroom().equals(newCourtroom) && 
                        m.getMeetingDate().equals(newDate) &&
                        m.getHour().equals(newTime)) {
                        JOptionPane.showMessageDialog(this, 
                                "This courtroom is already booked at the selected time.", 
                                "Unavailable", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Update meeting
                meeting.setMeetingDate(newDate);
                meeting.setHour(newTime);
                meeting.setCourtroom(newCourtroom);
                
                JOptionPane.showMessageDialog(this, 
                        "Meeting rescheduled successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Save changes
                Main.save();
                
                // Refresh display
                loadMeetings();
                
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
     * Cancels the selected meeting
     */
    private void cancelMeeting() {
        int selectedRow = meetingsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert to model index in case of sorting
        int modelRow = meetingsTable.convertRowIndexToModel(selectedRow);
        int meetingId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Find meeting
        Meeting meeting = null;
        Case caseObj = null;
        for (Case c : lawyer.getCasesHandled()) {
            for (Meeting m : c.getMeetingsList()) {
                if (m.getMeetingID() == meetingId) {
                    meeting = m;
                    caseObj = c;
                    break;
                }
            }
            if (meeting != null) {
                break;
            }
        }
        
        if (meeting == null || caseObj == null) return;
        
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to cancel this meeting?\n\n" +
                "Date: " + dateFormat.format(meeting.getMeetingDate()) + "\n" +
                "Time: " + (meeting.getHour() != null ? timeFormat.format(meeting.getHour()) : "-") + "\n" +
                "Case: " + caseObj.getCode(), 
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Remove meeting
        if (court.removeMeeting(meeting)) {
            JOptionPane.showMessageDialog(this, 
                    "Meeting cancelled successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Save changes
            Main.save();
            
            // Refresh display
            loadMeetings();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Failed to cancel meeting.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Checks if two dates are the same day
     */
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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