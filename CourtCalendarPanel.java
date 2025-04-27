package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import control.Court;
import enums.*;
import model.*;

/**
 * Panel for displaying and managing the court calendar
 * This is an additional feature - advanced calendar for meetings
 */
public class CourtCalendarPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Court court;
    private JTable calendarTable;
    private DefaultTableModel calendarModel;
    private JComboBox<String> monthComboBox;
    private JSpinner yearSpinner;
    private JComboBox<String> filterComboBox;
    private JPanel eventDetailsPanel;
    private JList<String> eventsList;
    private DefaultListModel<String> eventsModel;
    private JLabel detailsTitleLabel;
    private JTextArea detailsTextArea;
    private JPanel calendarHeader;
    
    private String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    
    private int currentMonth;
    private int currentYear;
    private Map<String, List<Meeting>> meetingsMap; // Map of date string to meetings list
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    /**
     * Create the frame for court calendar.
     */
    public CourtCalendarPanel() {
        this.court = Main.court;
        
        // Initialize with current month/year
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);
        
        setTitle("Court Calendar");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 900, 600);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create north panel with controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        contentPane.add(controlPanel, BorderLayout.NORTH);
        
        // Panel for month/year selection
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(datePanel, BorderLayout.WEST);
        
        JLabel monthLabel = new JLabel("Month:");
        datePanel.add(monthLabel);
        
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(currentMonth);
        monthComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentMonth = monthComboBox.getSelectedIndex();
                updateCalendar();
            }
        });
        datePanel.add(monthComboBox);
        
        JLabel yearLabel = new JLabel("Year:");
        datePanel.add(yearLabel);
        
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear - 10, currentYear + 10, 1));
        yearSpinner.addChangeListener(e -> {
            currentYear = (int) yearSpinner.getValue();
            updateCalendar();
        });
        datePanel.add(yearSpinner);
        
        // Panel for filter controls
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(filterPanel, BorderLayout.EAST);
        
        JLabel filterLabel = new JLabel("Filter By:");
        filterPanel.add(filterLabel);
        
        filterComboBox = new JComboBox<>(new String[] {
                "All Meetings", "Criminal Cases", "Financial Cases", "Family Cases"
        });
        filterComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCalendar();
            }
        });
        filterPanel.add(filterComboBox);
        
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Calendar today = Calendar.getInstance();
                currentMonth = today.get(Calendar.MONTH);
                currentYear = today.get(Calendar.YEAR);
                monthComboBox.setSelectedIndex(currentMonth);
                yearSpinner.setValue(currentYear);
                updateCalendar();
            }
        });
        filterPanel.add(todayButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCalendar();
            }
        });
        filterPanel.add(refreshButton);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Create calendar panel
        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        splitPane.setLeftComponent(calendarPanel);
        
        // Calendar header with weekday names
        calendarHeader = new JPanel(new GridLayout(1, 7));
        calendarHeader.setBackground(new Color(44, 62, 80));
        
        for (String weekday : weekdays) {
            JLabel dayLabel = new JLabel(weekday, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            calendarHeader.add(dayLabel);
        }
        
        calendarPanel.add(calendarHeader, BorderLayout.NORTH);
        
        // Calendar table
        calendarModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        calendarModel.setColumnCount(7); // 7 days in a week
        calendarModel.setRowCount(6);    // Maximum 6 weeks in a month
        
        calendarTable = new JTable(calendarModel);
        calendarTable.setRowHeight(80);
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarTable.setShowGrid(true);
        calendarTable.setIntercellSpacing(new Dimension(1, 1));
        calendarTable.setGridColor(Color.LIGHT_GRAY);
        
        // Custom renderer for calendar cells
        calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());
        
        // Add selection listener
        calendarTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateEventsList();
            }
        });
        
        calendarTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateEventsList();
            }
        });
        
        calendarPanel.add(new JScrollPane(calendarTable), BorderLayout.CENTER);
        
        // Create events panel
        eventDetailsPanel = new JPanel();
        eventDetailsPanel.setLayout(new BorderLayout());
        eventDetailsPanel.setBorder(new TitledBorder("Meeting Details"));
        splitPane.setRightComponent(eventDetailsPanel);
        
        // Events list
        eventsModel = new DefaultListModel<>();
        eventsList = new JList<>(eventsModel);
        eventsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateEventDetails();
            }
        });
        
        JScrollPane eventsScrollPane = new JScrollPane(eventsList);
        eventsScrollPane.setPreferredSize(new Dimension(0, 200));
        eventDetailsPanel.add(eventsScrollPane, BorderLayout.NORTH);
        
        // Event details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        eventDetailsPanel.add(detailsPanel, BorderLayout.CENTER);
        
        detailsTitleLabel = new JLabel("Select a meeting to view details");
        detailsTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsTitleLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
        detailsPanel.add(detailsTitleLabel, BorderLayout.NORTH);
        
        detailsTextArea = new JTextArea();
        detailsTextArea.setEditable(false);
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setWrapStyleWord(true);
        
        JScrollPane detailsScrollPane = new JScrollPane(detailsTextArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Initialize the calendar
        meetingsMap = new HashMap<>();
        loadMeetings();
        updateCalendar();
    }
    
    /**
     * Loads all meetings into the calendar
     */
    private void loadMeetings() {
        meetingsMap.clear();
        
        String filter = (String) filterComboBox.getSelectedItem();
        
        for (Meeting meeting : court.getAllMeetings().values()) {
            // Apply filter if selected
            if (!"All Meetings".equals(filter)) {
                Case caseObj = meeting.getCasee();
                
                if ("Criminal Cases".equals(filter) && !(caseObj instanceof CriminalCase)) {
                    continue;
                } else if ("Financial Cases".equals(filter) && !(caseObj instanceof FinancialCase)) {
                    continue;
                } else if ("Family Cases".equals(filter) && !(caseObj instanceof FamilyCase)) {
                    continue;
                }
            }
            
            // Format date for map key
            String dateKey = dateFormat.format(meeting.getMeetingDate());
            
            // Add meeting to map
            List<Meeting> dayMeetings = meetingsMap.getOrDefault(dateKey, new ArrayList<>());
            dayMeetings.add(meeting);
            meetingsMap.put(dateKey, dayMeetings);
        }
    }
    
    /**
     * Updates the calendar display
     */
    private void updateCalendar() {
        // Reload meetings with current filter
        loadMeetings();
        
        // Clear table
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                calendarModel.setValueAt(null, i, j);
            }
        }
        
        // Get first day of month
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);
        int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1; // Adjust for 0-indexed array
        
        // Get number of days in month
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Fill calendar
        int row = 0;
        int column = firstDayOfMonth;
        
        for (int day = 1; day <= daysInMonth; day++) {
            // Create calendar cell data
            cal.set(currentYear, currentMonth, day);
            String dateKey = dateFormat.format(cal.getTime());
            List<Meeting> dayMeetings = meetingsMap.getOrDefault(dateKey, new ArrayList<>());
            
            CalendarCellData cellData = new CalendarCellData(day, dayMeetings);
            calendarModel.setValueAt(cellData, row, column);
            
            // Move to next position
            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }
        
        // Update header
        String monthYear = months[currentMonth] + " " + currentYear;
        setTitle("Court Calendar - " + monthYear);
        
        // Clear event details
        eventsModel.clear();
        detailsTextArea.setText("");
        detailsTitleLabel.setText("Select a meeting to view details");
    }
    
    /**
     * Updates the events list based on selected date
     */
    private void updateEventsList() {
        int row = calendarTable.getSelectedRow();
        int col = calendarTable.getSelectedColumn();
        
        eventsModel.clear();
        
        if (row >= 0 && col >= 0) {
            Object value = calendarTable.getValueAt(row, col);
            
            if (value instanceof CalendarCellData) {
                CalendarCellData cellData = (CalendarCellData) value;
                
                // Create calendar to get the selected date
                Calendar cal = Calendar.getInstance();
                cal.set(currentYear, currentMonth, cellData.day);
                
                String dateKey = dateFormat.format(cal.getTime());
                List<Meeting> meetings = meetingsMap.getOrDefault(dateKey, new ArrayList<>());
                
                for (Meeting meeting : meetings) {
                    String timeStr = timeFormat.format(meeting.getHour());
                    String caseStr = meeting.getCasee().getCode();
                    String meetingStr = timeStr + " - " + caseStr;
                    eventsModel.addElement(meetingStr);
                }
                
                if (meetings.isEmpty()) {
                    detailsTitleLabel.setText("No meetings on this date");
                    detailsTextArea.setText("");
                } else {
                    detailsTitleLabel.setText("Select a meeting to view details");
                    detailsTextArea.setText("");
                }
            }
        }
    }
    
    /**
     * Updates the event details based on selected event
     */
    private void updateEventDetails() {
        int selectedIndex = eventsList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        
        int row = calendarTable.getSelectedRow();
        int col = calendarTable.getSelectedColumn();
        
        if (row >= 0 && col >= 0) {
            Object value = calendarTable.getValueAt(row, col);
            
            if (value instanceof CalendarCellData) {
                CalendarCellData cellData = (CalendarCellData) value;
                
                // Create calendar to get the selected date
                Calendar cal = Calendar.getInstance();
                cal.set(currentYear, currentMonth, cellData.day);
                
                String dateKey = dateFormat.format(cal.getTime());
                List<Meeting> meetings = meetingsMap.getOrDefault(dateKey, new ArrayList<>());
                
                if (selectedIndex < meetings.size()) {
                    Meeting meeting = meetings.get(selectedIndex);
                    displayMeetingDetails(meeting);
                }
            }
        }
    }
    
    /**
     * Displays the details of a meeting
     */
    private void displayMeetingDetails(Meeting meeting) {
        if (meeting == null) {
            return;
        }
        
        Case caseObj = meeting.getCasee();
        StringBuilder details = new StringBuilder();
        
        detailsTitleLabel.setText("Meeting Details - " + displayDateFormat.format(meeting.getMeetingDate()));
        
        details.append("Meeting ID: ").append(meeting.getMeetingID()).append("\n\n");
        details.append("Date: ").append(displayDateFormat.format(meeting.getMeetingDate())).append("\n");
        details.append("Time: ").append(timeFormat.format(meeting.getHour())).append("\n");
        details.append("Courtroom: ").append(meeting.getCourtroom().getCourtroomNumber())
               .append(" (").append(meeting.getCourtroom().getDepartment().getName()).append(")\n\n");
        
        details.append("CASE INFORMATION\n");
        details.append("----------------------------------------------\n");
        details.append("Case ID: ").append(caseObj.getCode()).append("\n");
        details.append("Case Type: ").append(caseObj.getCaseType()).append("\n");
        details.append("Status: ").append(caseObj.getCaseStatus()).append("\n");
        details.append("Open Date: ").append(displayDateFormat.format(caseObj.getOpenedDate())).append("\n\n");
        
        details.append("PEOPLE INVOLVED\n");
        details.append("----------------------------------------------\n");
        details.append("Accused: ").append(caseObj.getAccused().getFirstName())
                .append(" ").append(caseObj.getAccused().getLastName()).append("\n");
        details.append("Lawyer: ").append(caseObj.getLawyer().getFirstName())
                .append(" ").append(caseObj.getLawyer().getLastName()).append("\n");
        
        if (caseObj.getVerdict() != null) {
            details.append("\nVERDICT INFORMATION\n");
            details.append("----------------------------------------------\n");
            details.append("Verdict Date: ").append(displayDateFormat.format(caseObj.getVerdict().getIssusedDate())).append("\n");
            details.append("Judge: ").append(caseObj.getVerdict().getJudge().getFirstName())
                   .append(" ").append(caseObj.getVerdict().getJudge().getLastName()).append("\n");
            details.append("Summary: ").append(caseObj.getVerdict().getVerdictSummary()).append("\n");
        }
        
        detailsTextArea.setText(details.toString());
        detailsTextArea.setCaretPosition(0); // Scroll to top
    }
    
    /**
     * Class to hold data for a calendar cell
     */
    private class CalendarCellData {
        public int day;
        public List<Meeting> meetings;
        
        public CalendarCellData(int day, List<Meeting> meetings) {
            this.day = day;
            this.meetings = meetings;
        }
        
        @Override
        public String toString() {
            return String.valueOf(day);
        }
    }
    
    /**
     * Custom renderer for calendar cells
     */
    private class CalendarCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new BorderLayout());
            
            if (value instanceof CalendarCellData) {
                CalendarCellData data = (CalendarCellData) value;
                
                // Day number at top
                JLabel dayLabel = new JLabel(String.valueOf(data.day));
                dayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                dayLabel.setVerticalAlignment(SwingConstants.TOP);
                dayLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
                
                // Check if today
                Calendar cal = Calendar.getInstance();
                boolean isToday = cal.get(Calendar.YEAR) == currentYear && 
                                  cal.get(Calendar.MONTH) == currentMonth && 
                                  cal.get(Calendar.DAY_OF_MONTH) == data.day;
                
                if (isToday) {
                    dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD));
                    dayLabel.setForeground(new Color(41, 128, 185)); // Blue for today
                }
                
                panel.add(dayLabel, BorderLayout.NORTH);
                
                // Add meetings count if any
                if (data.meetings != null && !data.meetings.isEmpty()) {
                    JPanel meetingsPanel = new JPanel();
                    meetingsPanel.setLayout(new BoxLayout(meetingsPanel, BoxLayout.Y_AXIS));
                    
                    // Show only first 3 meetings to avoid crowding
                    int count = Math.min(data.meetings.size(), 3);
                    for (int i = 0; i < count; i++) {
                        Meeting meeting = data.meetings.get(i);
                        JLabel meetingLabel = new JLabel(timeFormat.format(meeting.getHour()) + " - " + meeting.getCasee().getCode());
                        meetingLabel.setFont(new Font("Arial", Font.PLAIN, 9));
                        meetingsPanel.add(meetingLabel);
                    }
                    
                    // If there are more meetings, show count
                    if (data.meetings.size() > 3) {
                        JLabel moreLabel = new JLabel("+" + (data.meetings.size() - 3) + " more");
                        moreLabel.setFont(new Font("Arial", Font.ITALIC, 9));
                        meetingsPanel.add(moreLabel);
                    }
                    
                    panel.add(meetingsPanel, BorderLayout.CENTER);
                    
                    // Set background color based on meeting count
                    if (data.meetings.size() >= 5) {
                        panel.setBackground(new Color(231, 76, 60, 100)); // Red for busy days
                    } else if (data.meetings.size() >= 3) {
                        panel.setBackground(new Color(243, 156, 18, 100)); // Orange for medium busy
                    } else {
                        panel.setBackground(new Color(46, 204, 113, 100)); // Green for light schedule
                    }
                }
            }
            
            // Selected cell highlighting
            if (isSelected) {
                panel.setBorder(new LineBorder(new Color(41, 128, 185), 2));
            } else {
                panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            }
            
            return panel;
        }
    }
}
