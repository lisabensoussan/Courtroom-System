package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import control.Court;
import enums.Gender;
import model.Accused;

/**
 * Read-only view for Accused individuals
 */
public class AccusedAdmin extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable accusedTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Court court;

    /**
     * Create the panel for viewing accused individuals
     */
    public AccusedAdmin() {
        court = Main.court;
        
        setTitle("Accused Records");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 900, 650); // Increased window size
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Search panel at the top
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(new CompoundBorder(
                new TitledBorder("Search"),
                new EmptyBorder(5, 5, 5, 5)));
        contentPane.add(searchPanel, BorderLayout.NORTH);
        
        JLabel searchLabel = new JLabel("Search: ");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JButton clearSearchButton = new JButton("Clear");
        searchPanel.add(clearSearchButton, BorderLayout.EAST);
        
        // Detail panel for viewing selected accused info - more compact design
        JPanel detailContainerPanel = new JPanel(new BorderLayout());
        detailContainerPanel.setPreferredSize(new Dimension(getWidth(), 100)); // Smaller height
        
        JPanel detailPanel = new JPanel();
        detailPanel.setBorder(new TitledBorder("Selected Accused Details"));
        detailPanel.setLayout(new GridLayout(3, 6, 5, 5)); // More compact grid with 3 rows and 6 columns
        detailContainerPanel.add(detailPanel, BorderLayout.CENTER);
        
        contentPane.add(detailContainerPanel, BorderLayout.SOUTH);
        
        // Add detail labels - compact layout
        Font boldFont = new Font(UIManager.getFont("Label.font").getName(), Font.BOLD, UIManager.getFont("Label.font").getSize());
        
        // Row 1
        detailPanel.add(new JLabel("ID:"));
        JLabel idValueLabel = new JLabel();
        idValueLabel.setFont(boldFont);
        detailPanel.add(idValueLabel);
        
        detailPanel.add(new JLabel("Name:"));
        JLabel nameValueLabel = new JLabel();
        nameValueLabel.setFont(boldFont);
        detailPanel.add(nameValueLabel);
        
        detailPanel.add(new JLabel("Job:"));
        JLabel jobValueLabel = new JLabel();
        jobValueLabel.setFont(boldFont);
        detailPanel.add(jobValueLabel);
        
        // Row 2
        detailPanel.add(new JLabel("Address:"));
        JLabel addressValueLabel = new JLabel();
        addressValueLabel.setFont(boldFont);
        detailPanel.add(addressValueLabel);
        
        detailPanel.add(new JLabel("Email:"));
        JLabel emailValueLabel = new JLabel();
        emailValueLabel.setFont(boldFont);
        detailPanel.add(emailValueLabel);
        
        detailPanel.add(new JLabel("Phone:"));
        JLabel phoneValueLabel = new JLabel();
        phoneValueLabel.setFont(boldFont);
        detailPanel.add(phoneValueLabel);
        
        // Row 3
        detailPanel.add(new JLabel("Birth Date:"));
        JLabel birthDateValueLabel = new JLabel();
        birthDateValueLabel.setFont(boldFont);
        detailPanel.add(birthDateValueLabel);
        
        detailPanel.add(new JLabel("Gender:"));
        JLabel genderValueLabel = new JLabel();
        genderValueLabel.setFont(boldFont);
        detailPanel.add(genderValueLabel);
        
        // Close button in the last cells
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> doDefaultCloseAction());
        buttonPanel.add(closeButton);
        detailPanel.add(buttonPanel);
        detailPanel.add(new JLabel()); // Empty cell for spacing
        
        // Table in center
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Address");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Birth Date");
        tableModel.addColumn("Job");
        tableModel.addColumn("Gender");
        
        accusedTable = new JTable(tableModel);
        accusedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accusedTable.setAutoCreateRowSorter(true);
        scrollPane.setViewportView(accusedTable);
        
        // Set up the sorter for searching
        sorter = new TableRowSorter<>(tableModel);
        accusedTable.setRowSorter(sorter);
        
        // Load data
        loadAccusedData();
        
        // Add event listeners
        // For search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });
        
        // Clear search button
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            filterTable();
        });
        
        // Selection listener for showing details
        accusedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = accusedTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedRow = accusedTable.convertRowIndexToModel(selectedRow);
                    
                    // Set values with better formatting and truncation for compact display
                    idValueLabel.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    
                    String fullName = tableModel.getValueAt(selectedRow, 1).toString() + " " + 
                                     tableModel.getValueAt(selectedRow, 2).toString();
                    nameValueLabel.setText(truncateIfNeeded(fullName, 30));
                    
                    jobValueLabel.setText(truncateIfNeeded(tableModel.getValueAt(selectedRow, 7).toString(), 20));
                    addressValueLabel.setText(truncateIfNeeded(tableModel.getValueAt(selectedRow, 3).toString(), 25));
                    emailValueLabel.setText(truncateIfNeeded(tableModel.getValueAt(selectedRow, 4).toString(), 25));
                    phoneValueLabel.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    birthDateValueLabel.setText(tableModel.getValueAt(selectedRow, 6).toString());
                    genderValueLabel.setText(tableModel.getValueAt(selectedRow, 8).toString());
                }
            }
        });
        
        // Add window controls for macOS
        addWindowControls();
    }
    
    /**
     * Adds window control button functionality
     */
    private void addWindowControls() {
        // This method enables the standard macOS window controls (red, yellow, green)
        
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
     * Filter the table based on search text
     */
    /**
     * Filter the table based on search text
     */
    private void filterTable() {
        String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Search in all columns
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    /**
     * Truncates text if it exceeds maxLength, adding "..." at the end
     */
    private String truncateIfNeeded(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Loads accused individuals into the table
     */
    private void loadAccusedData() {
        tableModel.setRowCount(0); // Clear the table before reloading
        
        for (Accused accused : court.getAllAccuseds().values()) {
            tableModel.addRow(new Object[]{
                accused.getId(),
                accused.getFirstName(),
                accused.getLastName(),
                accused.getAddress(),
                accused.getEmail(),
                accused.getPhoneNumber(),
                accused.getBirthDate() != null ? dateFormat.format(accused.getBirthDate()) : "N/A",
                accused.getJob(),
                accused.getGender()
            });
        }
    }
}