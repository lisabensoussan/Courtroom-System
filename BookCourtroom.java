package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import control.Court;
import enums.Specialization;
import model.*;

/**
 * This class provides an interface for employees to view available courtrooms,
 * book courtrooms for meetings, and manage the courtroom schedule.
 */
public class BookCourtroom extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    
    // Court singleton instance
    private Court court;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable availabilityTable;
    private JTable scheduleTable;
    private DefaultTableModel availabilityModel;
    private DefaultTableModel scheduleModel;
    private JComboBox<Courtroom> courtroomCombo;
    private JTextField dateField;
    private JComboBox<String> timeCombo;
    private JComboBox<Case> caseCombo;
    private JTextField participantsField;
    private JComboBox<String> discussionTypeCombo;
    private JTextArea notesArea;
    
    // Date formatting
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    /**
     * Create the internal frame for booking courtrooms
     */
    public BookCourtroom() {
        super("Courtroom Booking System", true, true, true, true);
        this.court = Main.court;
        
        setBounds(50, 50, 900, 600);
        
        // Initialize the user interface
        initializeUI();
        
        // Populate initial data
        populateCourtroomCombo();
        populateCaseCombo();
        loadSchedule();
        
        // Make the frame visible
        setVisible(true);
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        // Create main content panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create tabbed pane for different views
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        
        // Create tabs
        tabbedPane.addTab("Check Availability", createAvailabilityPanel());
        tabbedPane.addTab("Book Courtroom", createBookingPanel());
        tabbedPane.addTab("Courtroom Schedule", createSchedulePanel());
        
        // Start with the first tab
        tabbedPane.setSelectedIndex(0);
    }
    
    /**
     * Creates the panel for checking courtroom availability
     */
    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create a search panel at the top
        JPanel searchPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Available Courtrooms"));
        
        // Date input
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(new JLabel("Date (dd/MM/yyyy):"), BorderLayout.NORTH);
        dateField = new JTextField(dateFormat.format(new Date()));
        datePanel.add(dateField, BorderLayout.CENTER);
        searchPanel.add(datePanel);
        
        // Time input
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(new JLabel("Time:"), BorderLayout.NORTH);
        timeCombo = new JComboBox<>(new String[] {
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
        });
        timePanel.add(timeCombo, BorderLayout.CENTER);
        searchPanel.add(timePanel);
        
        // Department filter
        JPanel deptPanel = new JPanel(new BorderLayout());
        deptPanel.add(new JLabel("Department:"), BorderLayout.NORTH);
        JComboBox<Department> deptCombo = new JComboBox<>();
        deptCombo.addItem(null); // All departments option
        for (Department dept : court.getAllDepartments().values()) {
            deptCombo.addItem(dept);
        }
        deptCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    value = "All Departments";
                } else if (value instanceof Department) {
                    value = ((Department) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        deptPanel.add(deptCombo, BorderLayout.CENTER);
        searchPanel.add(deptPanel);
        
        // Search button
        JPanel searchButtonPanel = new JPanel(new BorderLayout());
        searchButtonPanel.add(new JLabel(" "), BorderLayout.NORTH); // Empty label for alignment
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse date and time
                    Date selectedDate = dateFormat.parse(dateField.getText());
                    String selectedTimeStr = (String) timeCombo.getSelectedItem();
                    java.sql.Time selectedTime = java.sql.Time.valueOf(selectedTimeStr + ":00");
                    
                    // Get selected department
                    Department selectedDept = (Department) deptCombo.getSelectedItem();
                    
                    // Search for available courtrooms
                    searchAvailableCourtrooms(selectedDate, selectedTime, selectedDept);
                    
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Invalid date format. Please use dd/MM/yyyy",
                            "Format Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        searchButtonPanel.add(searchButton, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Create table for displaying availability results
        availabilityModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the "Book" column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) return Boolean.class; // Book column is boolean
                return super.getColumnClass(column);
            }
        };
        
        // Add columns to the model
        availabilityModel.addColumn("Room #");
        availabilityModel.addColumn("Department");
        availabilityModel.addColumn("Features");
        availabilityModel.addColumn("Status");
        availabilityModel.addColumn("Book");
        
        availabilityTable = new JTable(availabilityModel);
        availabilityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availabilityTable.setRowHeight(25);
        
        // Add selection listener
        availabilityTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = availabilityTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // When a row is selected, we could display additional details
                    }
                }
            }
        });
        
                    // Add cell click listener for the "Book" column
        availabilityTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = availabilityTable.rowAtPoint(e.getPoint());
                int col = availabilityTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 4) { // Book column
                    Boolean isChecked = (Boolean) availabilityTable.getValueAt(row, 4);
                    if (isChecked != null && isChecked) {
                        // Get the courtroom number
                        int roomNumber = Integer.parseInt(availabilityTable.getValueAt(row, 0).toString());
                        Courtroom selectedRoom = court.getRealCourtroom(roomNumber);
                        
                        // Switch to booking tab and pre-fill with selected courtroom
                        tabbedPane.setSelectedIndex(1);
                        courtroomCombo.setSelectedItem(selectedRoom);
                        // Keep the date and time from the search
                    }
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(availabilityTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add bottom info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Select a courtroom and click the checkbox in the 'Book' column to proceed with booking"));
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the panel for booking a courtroom
     */
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create booking form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        
        // Courtroom selection
        formPanel.add(new JLabel("Courtroom:"));
        courtroomCombo = new JComboBox<>();
        courtroomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Courtroom) {
                    Courtroom room = (Courtroom) value;
                    Department dept = room.getDepartment();
                    value = "Room " + room.getCourtroomNumber() + 
                            (dept != null ? " (" + dept.getName() + ")" : "");
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        formPanel.add(courtroomCombo);
        
        // Date selection
        formPanel.add(new JLabel("Date (dd/MM/yyyy):"));
        JTextField bookingDateField = new JTextField(dateFormat.format(new Date()));
        formPanel.add(bookingDateField);
        
        // Time selection
        formPanel.add(new JLabel("Time:"));
        JComboBox<String> bookingTimeCombo = new JComboBox<>(new String[] {
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
        });
        formPanel.add(bookingTimeCombo);
        
        // Case selection
        formPanel.add(new JLabel("Case:"));
        caseCombo = new JComboBox<>();
        caseCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Case) {
                    Case caseObj = (Case) value;
                    value = caseObj.getCode() + " - " + caseObj.getCaseType() + 
                            " (" + caseObj.getAccused().getFirstName() + " " + 
                            caseObj.getAccused().getLastName() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        formPanel.add(caseCombo);
        
        // Number of participants
        formPanel.add(new JLabel("Expected Attendees:"));
        participantsField = new JTextField("10");
        formPanel.add(participantsField);
        
        // Discussion type
        formPanel.add(new JLabel("Discussion Type:"));
        discussionTypeCombo = new JComboBox<>(new String[] {
            "Hearing", "Trial", "Pre-Trial Conference", "Arraignment", "Sentencing", "Other"
        });
        formPanel.add(discussionTypeCombo);
        
        // Notes
        formPanel.add(new JLabel("Additional Notes:"));
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset form fields
                courtroomCombo.setSelectedIndex(0);
                bookingDateField.setText(dateFormat.format(new Date()));
                bookingTimeCombo.setSelectedIndex(0);
                caseCombo.setSelectedIndex(0);
                participantsField.setText("10");
                discussionTypeCombo.setSelectedIndex(0);
                notesArea.setText("");
            }
        });
        buttonsPanel.add(clearButton);
        
        JButton bookButton = new JButton("Book Courtroom");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate inputs
                    if (courtroomCombo.getSelectedItem() == null || caseCombo.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(panel,
                                "Please select a courtroom and case",
                                "Missing Information",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Parse date and time
                    Date selectedDate = dateFormat.parse(bookingDateField.getText());
                    String selectedTimeStr = (String) bookingTimeCombo.getSelectedItem();
                    java.sql.Time selectedTime = java.sql.Time.valueOf(selectedTimeStr + ":00");
                    
                    // Check if date is in the future
                    if (selectedDate.before(new Date())) {
                        JOptionPane.showMessageDialog(panel,
                                "Booking date must be in the future",
                                "Invalid Date",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Get selected courtroom and case
                    Courtroom selectedRoom = (Courtroom) courtroomCombo.getSelectedItem();
                    Case selectedCase = (Case) caseCombo.getSelectedItem();
                    
                    // Check for conflicts
                    for (Meeting m : court.getAllMeetings().values()) {
                        if (m.getCourtroom().equals(selectedRoom) &&
                            m.getMeetingDate().equals(selectedDate) &&
                            m.getHour().equals(selectedTime)) {
                            
                            JOptionPane.showMessageDialog(panel,
                                    "This courtroom is already booked at the selected time",
                                    "Booking Conflict",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    
                    // Create new meeting
                    Meeting newMeeting = new Meeting(selectedDate, selectedTime, selectedRoom, selectedCase);
                    
                    // Add to case and court
                    if (selectedCase.addMeeting(newMeeting) && court.addMeeting(newMeeting)) {
                        // Add notes as custom property if needed
                        // newMeeting.setNotes(notesArea.getText());
                        
                        JOptionPane.showMessageDialog(panel,
                                "Courtroom booked successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        // Save changes
                        Main.save();
                        
                        // Refresh schedule
                        loadSchedule();
                        
                        // Reset form
                        clearButton.doClick();
                    } else {
                        JOptionPane.showMessageDialog(panel,
                                "Failed to book courtroom",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Invalid date format. Please use dd/MM/yyyy",
                            "Format Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonsPanel.add(bookButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the panel for viewing the courtroom schedule
     */
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create filters panel
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
        // Courtroom filter
        filtersPanel.add(new JLabel("Courtroom:"));
        JComboBox<Object> scheduleRoomCombo = new JComboBox<>();
        scheduleRoomCombo.addItem("All Courtrooms");
        for (Courtroom room : court.getAllCourtrooms().values()) {
            scheduleRoomCombo.addItem(room);
        }
        scheduleRoomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Courtroom) {
                    Courtroom room = (Courtroom) value;
                    value = "Room " + room.getCourtroomNumber();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        filtersPanel.add(scheduleRoomCombo);
        
        // Date filter
        filtersPanel.add(new JLabel("Date:"));
        JTextField scheduleDateField = new JTextField(dateFormat.format(new Date()), 10);
        filtersPanel.add(scheduleDateField);
        
        // Filter button
        JButton filterButton = new JButton("Apply Filters");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse date
                    Date filterDate = null;
                    try {
                        filterDate = dateFormat.parse(scheduleDateField.getText());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(panel,
                                "Invalid date format. Please use dd/MM/yyyy",
                                "Format Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Get filter values
                    Object roomFilter = scheduleRoomCombo.getSelectedItem();
                    
                    // Apply filters to schedule
                    filterSchedule(roomFilter, filterDate);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Error applying filters: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        filtersPanel.add(filterButton);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSchedule();
            }
        });
        filtersPanel.add(refreshButton);
        
        panel.add(filtersPanel, BorderLayout.NORTH);
        
        // Create table for displaying schedule
        scheduleModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable
            }
        };
        
        // Add columns
        scheduleModel.addColumn("Meeting ID");
        scheduleModel.addColumn("Courtroom");
        scheduleModel.addColumn("Date");
        scheduleModel.addColumn("Time");
        scheduleModel.addColumn("Case");
        scheduleModel.addColumn("Case Type");
        scheduleModel.addColumn("Accused");
        
        scheduleTable = new JTable(scheduleModel);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setRowHeight(25);
        
        // Add context menu for canceling meetings
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cancelItem = new JMenuItem("Cancel Meeting");
        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = scheduleTable.getSelectedRow();
                if (selectedRow != -1) {
                    int meetingId = Integer.parseInt(scheduleTable.getValueAt(selectedRow, 0).toString());
                    cancelMeeting(meetingId);
                }
            }
        });
        popupMenu.add(cancelItem);
        
        // Add popup trigger
        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    
                    if (!source.isRowSelected(row)) {
                        source.changeSelection(row, column, false, false);
                    }
                    
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    
                    if (!source.isRowSelected(row)) {
                        source.changeSelection(row, column, false, false);
                    }
                    
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Populates the courtroom combo box with available courtrooms
     */
    private void populateCourtroomCombo() {
        courtroomCombo.removeAllItems();
        
        for (Courtroom room : court.getAllCourtrooms().values()) {
            courtroomCombo.addItem(room);
        }
    }
    
    /**
     * Populates the case combo box with active cases
     */
    private void populateCaseCombo() {
        caseCombo.removeAllItems();
        
        for (Case c : court.getAllCases().values()) {
            if (c.getCaseStatus() == enums.Status.inProcess) {
                caseCombo.addItem(c);
            }
        }
    }
    
    /**
     * Searches for available courtrooms based on criteria
     */
    private void searchAvailableCourtrooms(Date date, java.sql.Time time, Department department) {
        // Clear existing data
        availabilityModel.setRowCount(0);
        
        // Get all courtrooms
        Collection<Courtroom> rooms = court.getAllCourtrooms().values();
        
        // Get all meetings for the date
        List<Meeting> meetingsOnDate = new ArrayList<>();
        for (Meeting m : court.getAllMeetings().values()) {
            if (isSameDay(m.getMeetingDate(), date)) {
                meetingsOnDate.add(m);
            }
        }
        
        // Check each courtroom
        for (Courtroom room : rooms) {
            // Filter by department if specified
            if (department != null && !room.getDepartment().equals(department)) {
                continue;
            }
            
            // Check availability
            boolean isAvailable = true;
            for (Meeting m : meetingsOnDate) {
                if (m.getCourtroom().equals(room) && m.getHour().equals(time)) {
                    isAvailable = false;
                    break;
                }
            }
            
            // Add to table
            availabilityModel.addRow(new Object[] {
                room.getCourtroomNumber(),
                room.getDepartment() != null ? room.getDepartment().getName() : "None",
                "Standard", // No features in the Courtroom class, so using a default value
                isAvailable ? "Available" : "Booked",
                isAvailable ? Boolean.FALSE : null // Can only book if available
            });
        }
        
        // Sort by availability (available rooms first)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(availabilityModel);
        sorter.setComparator(3, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.equals("Available") && !o2.equals("Available")) {
                    return -1;
                } else if (!o1.equals("Available") && o2.equals("Available")) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });
        availabilityTable.setRowSorter(sorter);
        
        // Apply the sort
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }
    
    /**
     * Loads the schedule of all courtroom bookings
     */
    private void loadSchedule() {
        // Clear existing data
        scheduleModel.setRowCount(0);
        
        // Get all meetings sorted by date
        List<Meeting> meetings = new ArrayList<>(court.getAllMeetings().values());
        
        // Sort by date and time
        Collections.sort(meetings, new Comparator<Meeting>() {
            @Override
            public int compare(Meeting m1, Meeting m2) {
                int dateComp = m1.getMeetingDate().compareTo(m2.getMeetingDate());
                if (dateComp != 0) {
                    return dateComp;
                }
                return m1.getHour().compareTo(m2.getHour());
            }
        });
        
        // Add each meeting to the table
        for (Meeting m : meetings) {
            Case c = m.getCasee();
            
            scheduleModel.addRow(new Object[] {
                m.getMeetingID(),
                "Room " + m.getCourtroom().getCourtroomNumber(),
                dateFormat.format(m.getMeetingDate()),
                timeFormat.format(m.getHour()),
                c.getCode(),
                c.getCaseType(),
                c.getAccused().getFirstName() + " " + c.getAccused().getLastName()
            });
        }
    }
    
    /**
     * Filters the schedule based on criteria
     */
    private void filterSchedule(Object roomFilter, Date date) {
        // Clear existing data
        scheduleModel.setRowCount(0);
        
        // Get all meetings
        Collection<Meeting> allMeetings = court.getAllMeetings().values();
        
        // Filter meetings
        for (Meeting m : allMeetings) {
            // Filter by courtroom
            if (roomFilter instanceof Courtroom && !m.getCourtroom().equals(roomFilter)) {
                continue;
            }
            
            // Filter by date
            if (date != null && !isSameDay(m.getMeetingDate(), date)) {
                continue;
            }
            
            // Get case details
            Case c = m.getCasee();
            
            // Add meeting to table
            scheduleModel.addRow(new Object[] {
                m.getMeetingID(),
                "Room " + m.getCourtroom().getCourtroomNumber(),
                dateFormat.format(m.getMeetingDate()),
                timeFormat.format(m.getHour()),
                c.getCode(),
                c.getCaseType(),
                c.getAccused().getFirstName() + " " + c.getAccused().getLastName()
            });
        }
    }
    
    /**
     * Cancels a meeting with confirmation
     */
    private void cancelMeeting(int meetingId) {
        // Confirm cancellation
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this meeting?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);
                
        if (result == JOptionPane.YES_OPTION) {
            Meeting meeting = court.getRealMeeting(meetingId);
            
            if (meeting != null) {
                // Remove meeting from case
                Case c = meeting.getCasee();
                if (c != null) {
                    c.removeMeeting(meeting);
                }
                
                // Remove meeting from court
                if (court.removeMeeting(meeting)) {
                    JOptionPane.showMessageDialog(this,
                            "Meeting canceled successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Save changes
                    Main.save();
                    
                    // Refresh schedule
                    loadSchedule();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to cancel meeting",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not find the selected meeting",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Helper method to determine if two dates are on the same day
     */
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}